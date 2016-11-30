package org.rdfslice.graph.process;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;

import org.apache.log4j.Logger;
import org.rdfslice.RDFFileIterable;
import org.rdfslice.model.ModelFactory;
import org.rdfslice.model.Statement;
import org.rdfslice.model.Triple;
import org.rdfslice.query.BasicGraphPattern;
import org.rdfslice.util.DebugUtil;
import org.rdfslice.util.FileUtil;
import org.rdfslice.util.PatternUtil;
import org.rdfslice.util.RDFUtil;

public class OSSubjectOrderedPatternProcess {
	static Logger logger = Logger.getLogger(OSSubjectOrderedPatternProcess.class);
	
	public static void process(RandomAccessFile accessFile,
			BasicGraphPattern BGPattern, String format,
			PrintStream ps) throws Exception {
		FileChannel channel = accessFile.getChannel();
		InputStream stream = Channels.newInputStream(channel);
		RDFFileIterable rdfIS = new RDFFileIterable(stream, format);
		
		if(PatternUtil.isVariable(BGPattern.get(0).getSubject())) {
			for(Statement statement : rdfIS) {
				try {
					for(Triple triple : statement) {
						if(triple.match(BGPattern.get(0))) {
							if(match(triple.getObject(), BGPattern, 1, accessFile, ps)) {
								ps.println(triple);
							}
						}
					}
					DebugUtil.debug(statement.length(), logger);
				} catch (Exception e) {
					logger.error("Error parsing", e);
				}
			}
		} else {
			String subject = RDFUtil.cote(BGPattern.get(0).getSubject());
			long offset = FileUtil.binarySearch(accessFile, 0, accessFile.length(), subject);
			accessFile.seek(offset);			
			Statement statement = rdfIS.iterator().next();			
			for(Triple triple : statement) {
				if(triple.match(BGPattern.get(0))) {							
					if(match(triple.getObject(), BGPattern, 1, accessFile, ps)) {
						ps.println(triple);
					}
				}
			}
		}
	}
		
	public static boolean match(String subject, BasicGraphPattern BGPattern, int i, RandomAccessFile accessFile, PrintStream ps) throws Exception {
		String subjectCoted = RDFUtil.cote(subject);
		long offset = FileUtil.binarySearch(accessFile, 0, accessFile.length(), subjectCoted);
		accessFile.seek(offset);
		String line = "";
		boolean matched = false;
		while((line = accessFile.readLine()) !=null && line.startsWith(subjectCoted)) {
			Triple triple = ModelFactory.getNTriple(line);
			if(BGPattern.get(i).match(triple)) {
				ps.println(triple);
				matched = true;
			}
		}
		return matched;
	}
}
