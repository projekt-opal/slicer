package org.rdfslice;

import java.io.File;

public class Count {
	
	public static void main(String[] args) throws Exception {
		File mockOutPutFile = new File("/result.out");
		
		try {
			CountPrintSream count = new CountPrintSream(mockOutPutFile);
			String pattern = "Select * where {?s ?p ?o.}";			
			RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, RDFSliceStreamEngine.NO_ORDER, count, args);
			spe.run();
			
			System.out.println(count.getSize());
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
	}
	
	
}
