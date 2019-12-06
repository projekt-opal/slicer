package org.rdfslice;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.NTriplesParserSettings;

public class OpenRDF {

    public static void main(String... argv) throws Exception {
    	URL url = OpenRDF.class.getResource("/dbpedia_complexTriples.nt");
		final File source = new File(url.toURI());

		RDFParser rdfParser = Rio.createParser(RDFFormat.NTRIPLES);
		InputStream inputStream = InputStreamFactory.get(source.getCanonicalPath());
		
		rdfParser.getParserConfig().addNonFatalError(NTriplesParserSettings.FAIL_ON_NTRIPLES_INVALID_LINES);
		rdfParser.setRDFHandler(new RDFHandler() {
			
			@Override
			public void startRDF() throws RDFHandlerException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void handleStatement(Statement arg0) throws RDFHandlerException {
				System.out.println(arg0.getSubject().stringValue() +"  " + arg0.getObject().toString());
			}
			
			@Override
			public void handleNamespace(String arg0, String arg1)
					throws RDFHandlerException {
				// TODO Auto-generated method stub				
			}
			
			@Override
			public void handleComment(String arg0) throws RDFHandlerException {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void endRDF() throws RDFHandlerException {
				// TODO Auto-generated method stub				
			}
		});
		
		try {
			rdfParser.parse(inputStream, "");
		} catch (RDFParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RDFHandlerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}