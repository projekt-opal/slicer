package org.rdfslice;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

import junit.framework.Assert;

import org.junit.Test;
import org.rdfslice.model.Statement;
import org.rdfslice.util.FileUtil;

public class RDFInputStreamInteratorTest {
	
	@Test
	public void testRDFInputStreamInterator() throws Exception {
		URL url = RDFFileInteratorTest.class.getResource("/diseasome_slice.bz2");
		final File source = new File(url.toURI());
		final InputStream stream = InputStreamFactory.get(source.getCanonicalPath());
		Iterable<Statement> rdfIS = new Iterable<Statement>() {

			@Override
			public Iterator<Statement> iterator() {
				try {
					return new OpenRDFInputStreamInterator(stream, FileUtil.getFileFormat("/diseasome_slice.bz2"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		};
		
		int i=0;
		for(Statement st : rdfIS) {
			i += st.size();
		}
		
		Assert.assertEquals(98, i);
	}
	
	@Test
	public void testRDFInputStreamInterator1() throws Exception {
		URL url = RDFFileInteratorTest.class.getResource("/diseasome_slice.nt.bz2");
		final File source = new File(url.toURI());
		Iterable<Statement> rdfIS = new Iterable<Statement>() {

			@Override
			public Iterator<Statement> iterator() {
				try {
					return new OpenRDFInputStreamInterator(source.getCanonicalPath());
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		};
		
		int i=0;
		for(Statement st : rdfIS) {
			i += st.size();
		}
		
		Assert.assertEquals(98, i);
	}
	
	@Test
	public void testRDFInputStreamInterator2() throws Exception {
		URL url = RDFFileInteratorTest.class.getResource("/diseasome_slice.bz2");
		final File source = new File(url.toURI());
		Iterable<Statement> rdfIS = new Iterable<Statement>() {

			@Override
			public Iterator<Statement> iterator() {
				try {
					return new OpenRDFInputStreamInterator(source.getCanonicalPath());
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		};
		
		int i=0;
		for(Statement st : rdfIS) {
			i += st.size();
		}
		
		Assert.assertEquals(98, i);
	}
	
	@Test
	public void testRDFSliceJenaInputStreamInterator() throws Exception {
		URL url = RDFFileInteratorTest.class.getResource("/dbpedia_complexTriples.nt");
		File source = new File(url.toURI());
		final InputStream stream = InputStreamFactory.get(source.getCanonicalPath());
		Iterable<Statement> rdfIS = new Iterable<Statement>() {

			@Override
			public Iterator<Statement> iterator() {
				try {
					return new JenaRDFInputStreamInterator(stream);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		};
		
		int i=0;
		for(Statement st : rdfIS) {
			i++;
		}
		
		Assert.assertEquals(5, i);
	}
	
	@Test
	public void testRDFSliceJenaInputStreamInteratorBz2() throws Exception {
		URL url = RDFFileInteratorTest.class.getResource("/diseasome_slice.bz2");
		File source = new File(url.toURI());
		final InputStream stream = InputStreamFactory.get(source.getCanonicalPath());
		Iterable<Statement> rdfIS = new Iterable<Statement>() {

			@Override
			public Iterator<Statement> iterator() {
				try {
					return new JenaRDFInputStreamInterator(stream);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		};
		
		int i=0;
		for(Statement st : rdfIS) {
			i+= st.size();
		}
		
		Assert.assertEquals(98, i);
	}

}
