package org.rdfslice;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class CountPrintSream extends PrintStream {
		long triples = 0;
		
		public CountPrintSream(File file) throws FileNotFoundException {
			super(file);
		}

		@Override
		public void println(String x) {
			triples++;
		}
		
		public long getSize() {
			return triples;
		}
	}