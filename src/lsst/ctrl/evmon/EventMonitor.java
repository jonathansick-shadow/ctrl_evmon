package lsst.ctrl.evmon;

import java.util.Vector;

import lsst.ctrl.evmon.engine.EngineWorker;

/**
 * Class EventMonitor is the object that administers the execution of all Jobs given to it.
 * @author srp
 */

public class EventMonitor {
	Vector<Job> jobs = new Vector<Job>();
	
    /**
     * Class constructor which initializes the EventMonitor with a Job
     * @param job initial Job for the EventMonitor
     */
	public EventMonitor(Job job) {
		jobs.add(job);
	}
	
    /**
     * Class constructor which appends a job to the EventMonitor's job list
     * @param job Job to add to the EventMonitor's list
     */
	public void addJob(Job job) {
		jobs.add(job);
	}
	
    /**
     * Execute the Jobs on jobs list.  This method does not return.
     */
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
