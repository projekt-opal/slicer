package org.rdfslice.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.rdfslice.RDFFileIterable;
import org.rdfslice.model.IStatement;
import org.rdfslice.sort.ExternalSort;
import org.rdfslice.sort.FilePointer;

public class FileUtil {
	
	private static Charset charset = Charset.forName("UTF-8");

	public static void sort(File from, File to) throws IOException {
		FileInputStream fromStream = new FileInputStream(from);
		LineNumberReader fromBr = new LineNumberReader(new InputStreamReader(
				fromStream, charset));

		RandomAccessFile accessFile = new RandomAccessFile(to, "rw");

		String line = fromBr.readLine();
		while (line != null) {
			long offset = findPosition(accessFile, line);
			accessFile.seek(offset);
			accessFile.write(line.getBytes(), 1, line.length());
			line = fromBr.readLine();
		}
	}

	public static long findPosition(RandomAccessFile accessFile, String line)
			throws IOException {
		return binarySearch(accessFile, 0, accessFile.length(), line);
	}

	public static long binarySearch(RandomAccessFile accessFile, long startPos,
			long endPos, String line) throws IOException {

		startPos = getLineStartPosition(accessFile, startPos);
		endPos = getLineEndPosition(accessFile, endPos);

		String startLine = pickLine(accessFile, startPos);
		String endLine = pickLine(accessFile, endPos);

		if (startLine.equals(endLine)) {
			if (startLine.compareTo(line) > 0)
				return startPos;
			else
				return endPos;
		}

		int compareStart = startLine.compareTo(line);
		int compareEnd = endLine.compareTo(line);

		if (compareStart < 0 && compareEnd < 0) {
			return getLineEndPosition(accessFile, endPos);
		} else if (compareStart > 0 && compareEnd > 0) {
			return startPos;
		} else if (compareStart < 0 && compareEnd > 0) {
			long middle = (startPos + endPos) / 2;
			String middleLine = pickLine(accessFile, middle);
			middle = getLineEndPosition(accessFile, middle);
			middleLine = pickLine(accessFile, middle);

			if (middleLine.equals(endLine))
				return getLineEndPosition(accessFile,
						middle - middleLine.length());

			if (middleLine.equals(startLine))
				return getLineEndPosition(accessFile,
						middle + middleLine.length());

			if (middleLine.compareTo(line) >= 0)
				return binarySearch(accessFile, startPos,
						middle - (middleLine.length() + 1), line);
			else
				return binarySearch(accessFile, middle, endPos, line);
		}
		return 0;
	}

	public static String pickLine(RandomAccessFile accessFile, long pos)
			throws IOException {
		long pointer = getLineStartPosition(accessFile, pos);
		accessFile.seek(pointer);
		String readline  = accessFile.readLine();
		return new String(readline.getBytes(), "UTF-8");
	}

	public static long getLineStartPosition(RandomAccessFile accessFile,
			long pos) throws IOException {

		if (pos == 0)
			return 0;

		if (accessFile.length() == pos)
			pos = pos - 3;

		accessFile.seek(pos);
		int readByte = accessFile.read();
		long pointer = pos - 1;

		while ((readByte != 0xD) && (readByte != '\n') && ((pointer) != 0)) {
			accessFile.seek(pointer);
			readByte = accessFile.read();
			pointer = pointer - 1;
		}

		if (pointer == 0xD)
			return pointer + 3;
		else if(readByte == '\n')
			return pointer + 2;

		return pointer;
	}

	public static long getLineEndPosition(RandomAccessFile accessFile, long pos)
			throws IOException {

		accessFile.seek(pos);
		accessFile.readLine();

		return accessFile.getFilePointer();
	}

	public static void sort2(File file, File out) throws Exception {
		System.out.println("sorting");
		FileInputStream ras =new FileInputStream(
				file);
		RDFFileIterable rdfIterable = new RDFFileIterable(ras, getFileFormat(file));
		List<FilePointer> filePointerList = new ArrayList<FilePointer>();
		List<File> fileList = new ArrayList<File>();
		FilePointer filePointer = new FilePointer();

		fileList.add(filePointer.getFile());
		filePointerList.add(filePointer);

		for (IStatement pattern : rdfIterable) {
			filePointer = getFilePointer(filePointerList, pattern.toString());

			if (filePointer == null) {
				filePointer = new FilePointer();
				filePointerList.add(filePointer);
				fileList.add(filePointer.getFile());
			}

			filePointer.append(pattern.toString());
		}

		System.out.println(fileList.size());

		Comparator<String> cmp = new Comparator<String>() {
			public int compare(String r1, String r2) {
				return r1.compareTo(r2);
			}
		};

		System.out.println("merging");
		ExternalSort.mergeSortedFiles(fileList, out, cmp,
				Charset.defaultCharset());
	}

	public static FilePointer getFilePointer(List<FilePointer> filePointerList,
			String line) throws Exception {

		for (FilePointer filePointer : filePointerList) {
			String startLine = filePointer.getFistLine();
			String lastLine = filePointer.getLastLine();

			if (startLine == null && lastLine == null)
				return filePointer;

			else if (line.compareTo(lastLine) >= 0)
				return filePointer;
		}

		return null;
	}

	public static void delete(File file) throws IOException {
		if (file.isDirectory()) {
			// list all the directory contents
			String files[] = file.list();
			for (String temp : files) {
				// construct the file structure
				File fileDelete = new File(file, temp);
				// recursive delete
				delete(fileDelete);
			}
			// directory is empty, then delete it
			file.delete();
		} else
			file.delete();
	}
	
	public static String getFileFormat(String fileName) {
		String format = RDFFileIterable.NTRIPLES_FORMAT;
		
		if(fileName.contains(".nt.") || fileName.endsWith(".nt")) {
			format = RDFFileIterable.NTRIPLES_FORMAT;
		} else if(fileName.contains(".json.") || fileName.contains(".jsonld.") ||
				fileName.endsWith(".json") || fileName.endsWith(".jsonld")) {
			format = RDFFileIterable.JSONLD_FORMAT;
		} else if(fileName.contains(".nq.") || fileName.contains(".nquads.") || 
				fileName.endsWith(".nq") || fileName.endsWith(".nquads")) {
			format = RDFFileIterable.NQUADS_FORMAT;
		} else if(fileName.contains(".n3.")
				|| fileName.endsWith(".n3")) {
			format = RDFFileIterable.N3_FORMAT;
		} else if(fileName.contains(".ttl.")
				|| fileName.endsWith(".ttl")) {
			format = RDFFileIterable.TURTLE_FORMAT;
		} else if(fileName.contains(".xml.")
				|| fileName.endsWith(".xml")) {
			format = RDFFileIterable.XML_FORMAT;
		}
		
		return format;
	}
	
	public static String getFileFormat(File file) {				
		return getFileFormat(file.getName());
	}
}
