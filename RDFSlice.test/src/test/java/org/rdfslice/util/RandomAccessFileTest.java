package org.rdfslice.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;

public class RandomAccessFileTest {
	@Test
	public void testRandoAccess1 () throws IOException {
		InputStream is = new FileInputStream("C:/Users/research/lod/t/instance_types_en.ttl");
		BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
		
		String line = null;
		while((line = reader.readLine()) != null) {
			System.out.println(line);
			break;
		}
		reader.skip(1000000);
		while((line = reader.readLine()) != null) {
			System.out.println(line);
			break;
		}
		
		reader.skip(0);
		//reader.reset();
		//reader.skip(500);
		while((line = reader.readLine()) != null) {
			System.out.println(line);
			break;
		}
	}
}
