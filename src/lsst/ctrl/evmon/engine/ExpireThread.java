package lsst.ctrl.evmon.engine;

import lsst.ctrl.evmon.Condition;
import lsst.ctrl.evmon.ExceptionTask;
import lsst.ctrl.evmon.Link;

public class ExpireThread extends Thread {

	EngineWorker engine;
	ExceptionTask task;
	long time;
	ChainEnvironment chainEnv;
	Link conditionalLink;

	ExpireThread(EngineWorker engine, Condition cond, ChainEnvironment chainEnv) {
		this.conditionalLink = cond;
		this.task = cond.getExceptionTask();
		this.engine = engine;
		this.time = cond.getTimeout();
		this.chainEnv = chainEnv;
	}

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