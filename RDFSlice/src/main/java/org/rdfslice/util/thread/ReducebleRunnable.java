package org.rdfslice.util.thread;

public interface ReducebleRunnable<T> extends Runnable {	
	T reduce(T result);
	T getResult();
}
