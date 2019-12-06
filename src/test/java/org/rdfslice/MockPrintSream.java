package org.rdfslice;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class MockPrintSream extends PrintStream {
		long size = 0;
		public MockPrintSream(File file) throws FileNotFoundException {
			super(file);
		}

		@Override
		public void println(String x) {
			System.out.println(x);
			size++;
		}
		
		@Override
		public void println(Object x) {
			System.out.println(x);
			size++;
		}
		
		public long getSize() {
			return size;
		}
	}