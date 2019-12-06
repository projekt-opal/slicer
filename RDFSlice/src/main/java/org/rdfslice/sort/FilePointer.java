package org.rdfslice.sort;

import java.io.File;
import java.io.RandomAccessFile;

import org.rdfslice.util.FileUtil;

public class FilePointer {
		File file;
		boolean createNewFile;
		RandomAccessFile accessFile;
		
		public FilePointer() throws Exception {
			file = TmpFileManager.getInstace(null).get();
			file.deleteOnExit();
			accessFile = new RandomAccessFile(file, "rwd");
		}
		
		public String getFistLine() throws Exception{
			return FileUtil.pickLine(accessFile, 0);
		}
		
		public String getLastLine() throws Exception{
			return FileUtil.pickLine(accessFile, accessFile.length());
		}
		
		public void append(String line) throws Exception{
			accessFile.seek(accessFile.length());
			accessFile.write((line + "\r\n").getBytes());
		}

		public File getFile() {
			return file;
		}
	}