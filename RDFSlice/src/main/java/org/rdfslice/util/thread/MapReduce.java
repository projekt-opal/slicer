package org.rdfslice.util.thread;


public class MapReduce<M,R> {
	
	private ReducebleRunnable<R>[] runnables;
	
	public void map(ReducebleRunnable<R>... runnables) {
		this.runnables = runnables;
	}
	
	public R reduce() {
		MultiRunnerDam runnerDam = new MultiRunnerDam(runnables);
		runnerDam.run();
		R result = null;
		for(ReducebleRunnable<R>  runnable : runnables) {
			if(result != null) {
				result = runnable.reduce(result);
			} else {
				result = runnable.getResult();
			}
		}
		return result;
	}

}
