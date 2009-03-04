package lsst.ctrl.evmon;

import java.util.Vector;

import lsst.ctrl.evmon.engine.EngineWorker;


public class EventMonitor {
	Vector<Job> jobs = new Vector<Job>();
	
	public EventMonitor(Job job) {
		jobs.add(job);
	}
	
	public void addJob(Job job) {
		jobs.add(job);
	}
	
	public void runJobs() {
		for (int i = 0; i < jobs.size(); i++) {
			EngineWorker ew = new EngineWorker(jobs.get(i));
			ew.start();
		}
		System.out.println("really done");
		// TODO: we should .join here, not sleep
		for (;;) {
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
