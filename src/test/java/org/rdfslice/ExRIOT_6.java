package org.rdfslice;

import java.net.URISyntaxException;

public class ExRIOT_6 {

    public static void main(String... argv) throws URISyntaxException {
//    	URL url = RDFFileInteratorTest.class.getResource("/diseasome_slice.bz2");
//		final File source = new File(url.toURI());
//
//        // Create a PipedRDFStream to accept input and a PipedRDFIterator to
//        // consume it
//        // You can optionally supply a buffer size here for the
//        // PipedRDFIterator, see the documentation for details about recommended
//        // buffer sizes
//        PipedRDFIterator<Triple> iter = new PipedRDFIterator<Triple>();
//        final PipedRDFStream<Triple> inputStream = new PipedTriplesStream(iter);
//
//        // PipedRDFStream and PipedRDFIterator need to be on different threads
//        ExecutorService executor = Executors.newSingleThreadExecutor();
//
//        // Create a runnable for our parser thread
//        Runnable parser = new Runnable() {
//
//            @Override
//            public void run() {
//                // Call the parsing process.
//                try {
//					RDFDataMgr.parse(inputStream, source.getCanonicalPath());
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//            }
//        };
//
//        // Start the parser on another thread
//        executor.submit(parser);

        // We will consume the input on the main thread here

        // We can now iterate over data as it is parsed, parsing only runs as
        // far ahead of our consumption as the buffer size allows
//        while (iter.hasNext()) {
//            Triple next = iter.next();
//            System.out.println(next);
//            // Do something with each triple
//        }
    }

}