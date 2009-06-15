package lsst.ctrl.evmon;

import java.util.Vector;

import lsst.ctrl.evmon.engine.EngineWorker;
import java.util.ArrayList;

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
        ArrayList<EngineWorker> list = new ArrayList<EngineWorker>();
        for (int i = 0; i < jobs.size(); i++) {
            EngineWorker ew = new EngineWorker(jobs.get(i));
            ew.start();
            list.add(ew);
        }
        
        // run through the list, waiting on each thread to complete.
        // threads that have not completed will block, but will fall through
        // when completed.  threads that have already completed will fall through.
        for (int i = 0; i < list.size(); i++) {
            try {
                EngineWorker ew = list.get(i);
                ew.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
