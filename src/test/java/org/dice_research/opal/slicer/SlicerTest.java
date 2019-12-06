package org.dice_research.opal.slicer;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

public class SlicerTest {

	public static final String inputFile = "/tmp/mcloud.ttl";

	@Test
	public void test() throws Exception {

		File in = new File(inputFile);
		Assume.assumeTrue(in.canRead());

		File out = File.createTempFile(SlicerTest.class.getName(), ".txt");
		out.deleteOnExit();

		List<String> args = new LinkedList<>();

		args.add("-source");
		args.add(in.getAbsolutePath());

		args.add("-patterns");
		args.add("Select * where {?p ?k <http://www.w3.org/ns/dcat#Dataset>}");

		args.add("-out");
		args.add(out.getAbsolutePath());

		System.out.println("Arguments:   " + args);
		System.out.println("Input file:  " + in.getAbsolutePath());
		System.out.println("Output file: " + out.getAbsolutePath());

		Slicer.main(args.toArray(new String[args.size()]));

		Assert.assertTrue(out.exists());
		Assert.assertTrue(out.length() > 0);
	}

}
