package org.rdfslice.all;
import org.rdfslice.util.thread.MultiRunnerDam;


public class DamExample {
	public static void main(String[] args) {
		Runnable runnable1 = new Runnable() {
			
			@Override
			public void run() {
				String um = "1";
				System.out.println(um);		
			}
		};
		
		Runnable runnable2 = new Runnable() {
			
			@Override
			public void run() {
				String dois = "2";
				System.out.println(dois);
			}
		};
		
		MultiRunnerDam dam = new MultiRunnerDam(runnable1, runnable2);
		
		dam.run();
	}
}
