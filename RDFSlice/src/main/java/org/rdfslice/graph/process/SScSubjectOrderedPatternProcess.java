package org.rdfslice.graph.process;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;

import org.rdfslice.RDFFileIterable;
import org.rdfslice.model.Statement;
import org.rdfslice.query.BasicGraphPattern;
import org.rdfslice.util.FileUtil;
import org.rdfslice.util.RDFUtil;

public class SScSubjectOrderedPatternProcess {
	
	public static void process(RandomAccessFile accessFile,
			String format,
			BasicGraphPattern BGPattern,			
			PrintStream ps) throws Exception {
		String subject = RDFUtil.cote(BGPattern.get(0).getSubject());
		long offset = FileUtil.binarySearch(accessFile, 0, accessFile.length(), subject);
		accessFile.seek(offset);
		FileChannel channel = accessFile.getChannel();
		InputStream stream = Channels.newInputStream(channel);
		RDFFileIterable rdfIS = new RDFFileIterable(stream, format);
		if(rdfIS.iterator().hasNext()) {
			Statement statement = rdfIS.iterator().next();	
			if(statement == null) {
				return;
			}
			SSGeneralOrderedPatternProcess.process(statement, BGPattern, ps);
		}
	}
}
