package org.rdfslice;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.openrdf.rio.ParserConfig;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.BasicParserSettings;
import org.openrdf.rio.helpers.NTriplesParserSettings;
import org.openrdf.rio.ntriples.NTriplesUtil;
import org.rdfslice.model.Statement;
import org.rdfslice.model.Triple;
import org.rdfslice.util.FileUtil;

public class OpenRDFInputStreamInterator implements Iterator<Statement> {
	
	private Queue<Statement> queue  = new LinkedList<Statement>();
	
	private boolean finished = false;
	
	private InputStream is = null;
	
	private Thread readThread = null;
	
	private String fileName = null;
	
	private String format = RDFFileIterable.TRIPLES_FORMAT;	
	
	private Runnable runnable = new Runnable() {
		
		@Override
		public void run() {
			
			RDFParser rdfParser = null;
			
			if(fileName != null) {
				format = FileUtil.getFileFormat(fileName);		
			}
			
			if(is != null) {
				if(format.contains(RDFFileIterable.NTRIPLES_FORMAT)) {
					rdfParser = Rio.createParser(RDFFormat.NTRIPLES);
				} else if(format.contains(RDFFileIterable.JSONLD_FORMAT)) {
					rdfParser = Rio.createParser(RDFFormat.RDFJSON);
				} else if(format.contains(RDFFileIterable.NQUADS_FORMAT)) {
					rdfParser = Rio.createParser(RDFFormat.NQUADS);
				} else if(format.contains(RDFFileIterable.N3_FORMAT)) {
					rdfParser = Rio.createParser(RDFFormat.N3);
				} else if(format.contains(RDFFileIterable.TURTLE_FORMAT)) {
					rdfParser = Rio.createParser(RDFFormat.TURTLE);
				} else if(format.contains(RDFFileIterable.XML_FORMAT)) {
					rdfParser = Rio.createParser(RDFFormat.RDFXML);
				}
			}
			
			if(rdfParser == null){
				rdfParser = Rio.createParser(RDFFormat.NTRIPLES);
			}
			
			ParserConfig config = rdfParser.getParserConfig();
			config.addNonFatalError(NTriplesParserSettings.FAIL_ON_NTRIPLES_INVALID_LINES);
			config.addNonFatalError(BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES);
			config.addNonFatalError(BasicParserSettings.VERIFY_DATATYPE_VALUES);
			config.addNonFatalError(BasicParserSettings.NORMALIZE_DATATYPE_VALUES);
			config.set(BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES, false);
			
			config.set(BasicParserSettings.VERIFY_DATATYPE_VALUES, false);
			config.set(BasicParserSettings.VERIFY_RELATIVE_URIS, false);
			config.set(BasicParserSettings.VERIFY_LANGUAGE_TAGS, false);
			
			config.set(BasicParserSettings.NORMALIZE_DATATYPE_VALUES, false);
			config.set(BasicParserSettings.NORMALIZE_LANGUAGE_TAGS, false);			
			
			rdfParser.setRDFHandler(new RDFHandler() {
				Statement lastStatement = null;
				
				@Override
				public void startRDF() throws RDFHandlerException {
				}
				
				@Override
				public void handleStatement(org.openrdf.model.Statement arg0) throws RDFHandlerException {
					String subject = arg0.getSubject().toString();
					String predicate = arg0.getPredicate().toString();
					String object = arg0.getObject().toString();
										
					Triple t = new Triple(NTriplesUtil.toNTriplesString(arg0.getSubject()) + " " 
							+ NTriplesUtil.toNTriplesString(arg0.getPredicate()) + " "
							+ NTriplesUtil.toNTriplesString(arg0.getObject())+ " .");
					t.setObject(object);
					t.setSubject(subject);
					t.setPredicate(predicate);
					
					Statement st = null;
					if(lastStatement != null && lastStatement.getSubject().equals(t.getSubject())) {
						st = lastStatement;
					} else {
						st = new Statement();
						queue.add(st);
					}
					st.add(t);
					if(queue.size() >= 100 && lastStatement != st) {
						try {
							synchronized (queue) {
								queue.notifyAll();
								queue.wait();
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					lastStatement = st;
				}
				
				@Override
				public void handleNamespace(String arg0, String arg1)
						throws RDFHandlerException {
				}
				
				@Override
				public void handleComment(String arg0) throws RDFHandlerException {
				}
				
				@Override
				public void endRDF() throws RDFHandlerException {
					finished = true;
					synchronized (queue) {
						queue.notifyAll();
					}
					try {
						if(fileName != null) {
							is.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});	
			
			try {
				rdfParser.parse(is, "");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RDFParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RDFHandlerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	
	public OpenRDFInputStreamInterator(InputStream stream, String format) {
		this.is = stream;
		this.format = format;
	}

	public OpenRDFInputStreamInterator(String file) throws Exception {
		this.is = InputStreamFactory.get(file);
		this.fileName = file;
	}
	
	public OpenRDFInputStreamInterator(String file, String format) throws Exception {
		this.is = InputStreamFactory.get(file);
		this.fileName = file;
	}

	@Override
	public boolean hasNext() {
		if(readThread == null) { // if the read thread is not running yet
			readThread = new Thread(runnable);
			readThread.start();
		}
		if(queue.size() == 0 && !finished) {
			try {
				synchronized (queue) {
					queue.notifyAll();
					queue.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return  queue.size() > 0;
	}

	@Override
	public Statement next() {
		return queue.poll();
	}

	@Override
	public void remove() {
	}

}
