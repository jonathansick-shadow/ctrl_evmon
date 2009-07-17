package lsst.ctrl.evmon.engine;

import lsst.ctrl.evmon.Condition;
import lsst.ctrl.evmon.ExceptionTask;
import lsst.ctrl.evmon.Link;

/**
 * ExpireThread waits for a specified time, and if that time expires, it
 * executes a Task, and marks the Chain that owns it as expired so it can
 * be cleaned up by the EventMonitor.
 */
public class ExpireThread extends Thread {

	EngineWorker engine;
	ExceptionTask task;
	long time;
	ChainEnvironment chainEnv;
	Link conditionalLink;

    /**
     * Class constructor ExpireThread 
     * @param engine The EngineWorker that owns this ExpireThread
     * @param cond The Condition which specifies this ExpireThread
     * @param chainEnv The ChainEnvironment associated with this ExpireThread
     */ 
	ExpireThread(EngineWorker engine, Condition cond, ChainEnvironment chainEnv) {
		this.conditionalLink = cond;
		this.task = cond.getExceptionTask();
		this.engine = engine;
		this.time = cond.getTimeout();
		this.chainEnv = chainEnv;
	}

    /**
     * Run method that is executed for this thread.  A timer sleeps until
     * completion, or interruption.  If it's interrupted, it just returns. If
     * the timer completes, it checks the state of the Chain, and if it's
     * still alive and on the Link that sent it here in the first place, it
     * executes the exception, marks the ChainEnvironment for expiration, and
     * returns.
     */ 
	public void run() {
		try {
			sleep(time);
		} catch (InterruptedException e) {
			return;
		}
		if (chainEnv.state == ChainEnvironment.COMPLETED)
			return;

		// if we're not even on the same link, we've already moved on, so just
		// exit.
		if (chainEnv.getCurrentLink() != conditionalLink)
			return;
		
		// if the message we're currently looking at matches, then this
		// pending condition means we shouldn't throw an exception, and we
		// should move on.
		MonitorMessage msg = engine.getCurrentMessage();
		if (chainEnv.equals(msg) == true) {
			return;
		}

		task.execute(chainEnv.getEventStore());
		chainEnv.setExpired(true);

	}
}
