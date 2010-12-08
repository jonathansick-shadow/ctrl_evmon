package lsst.ctrl.evmon.engine;

import lsst.ctrl.evmon.Chain;
import lsst.ctrl.evmon.Condition;
import lsst.ctrl.evmon.Job;
import lsst.ctrl.evmon.Link;
import lsst.ctrl.evmon.Span;
import lsst.ctrl.evmon.Task;
import lsst.ctrl.evmon.input.MessageReader;

/**
 * Class EngineWorker performs the action of "running" a Job.  This entails
 * reading messages from the Reader specified in the Job and performing operations
 * as specified by the Chain until all the messages have been processed.
 */
public class EngineWorker extends Thread {
	Job job;
	MonitorMessage msg = null;

    /**
     * Class constructor EngineWorker
     * @param job the Job to "execute"
     */
	public EngineWorker(Job job) {
		this.job = job;
	}

    /**
     * This thread's run method
     */
	public void run() {
		runJob();
	}

    /** 
     * This is the main execution method of this object, and where all
     * the work gets done.
     */
    // Changes in how Chains are processed either need to take this method
    // into account.
    // Possible future changes include multiple Chains processing a single
    // Reader's messages, splitting each Chain into it's own thread, etc.
	public void runJob() {

		EnvironmentList condList = new EnvironmentList();

		MessageReader input = job.getReader();

		Chain chain = job.getChain();

		ChainEnvironment chainEnvironment = new ChainEnvironment(chain);

		condList.add(chainEnvironment);

		EnvironmentList replicationList = new EnvironmentList();
		EnvironmentList removeList = new EnvironmentList();

		// System.out.println("waiting");

		int temp_msgCount = 0;
		while ((setCurrentMessage(input.getMessage())) != null) {
			/*
			temp_msgCount++;
			// System.out.println(temp_msgCount);
			if (temp_msgCount >= 2000)
				; //System.out.println("boop");
			*/
			int size = condList.size();
			for (int i = 0; i < size; i++) {
				ChainEnvironment ce = condList.get(i);
				if (ce.isExpired()) {
					removeList.add(ce);
					continue;
				}

				Link link = ce.getCurrentLink();
				
				// If this is a Task (which can only be if it's the first thing
				// in the Chain), run through all the Tasks.
				//
				// At this point, one of three things can be true:
				// 1) The Chain is completed.  It's been marked, so we
				// just continue through the rest of the ChainEnvironments,
				// and clean-up will happen at the end.
				// 2) We hit a Condition that needs to be replicated because
				// it contains a Span.  Do that, and clear the replicationList.
				// 3) This is a new condition that might have an Exception
				// associated with it.  Register the exception thread, if
				// necessary.
				//
				// After all this, the next thing that can be on the Chain
				// is a condition, so deal with it as normal.
				if (link instanceof Task) {
					int pos = ce.getIndex();
					// if we were working on the first thing in the list
					// we need to replicate the whole thing.
					if (pos == 0) {
						replicationList.add(ce.clone());
						replicateEntries(condList, replicationList);
					}
					int state = runTasks(ce, removeList, replicationList);
					if (state == ce.COMPLETED) {
						continue;
					}
					if (state == ce.EXPAND_SPAN)
						replicateEntries(condList, replicationList);
					if (state == ce.PENDING)
						registerExceptionThread(ce);
				}

				// This is a condition, so evaluate it.
				if (ce.evaluate(msg) != true) {
					// System.out.println(this + ": no match on: "+ ce.getCurrentLink().toString());
					continue;
				}

				//System.out.println(this + ": condition returned true: "+ ce.getCurrentLink().toString());

				// if this is the first element, we have to be prepared
				// for the next incoming message, so add it to the list
				// of things to be replicated.
				int index = ce.getIndex();
				if (index == 0) {
					replicationList.add(ce.clone());
				}

				// store the message we just looked at
				ce.storeMessage(msg);

				// run through the rest of the list until you hit a Condition
				ce.updateChainPointer();
				int state = ce.runTasks(this, msg);

				// if we're done, remove this from the work list.
				// if we've hit a Condition that has a Span, add it to the
				// replication list and register an ExceptionThread,
				// if there is one.
				if (state == ce.COMPLETED) {
					removeList.add(ce);
				} else if (state == ce.EXPAND_SPAN) {
					Link currentLink = ce.getCurrentLink();
					if (currentLink instanceof Condition) {
						Condition cond = (Condition) currentLink;
						Span span = cond.getSpan();

						if (span != null) {
							for (int j = span.lower; j <= span.upper; j = j
									+ span.increment) {
								ChainEnvironment newCE = ce.clone();
								newCE.setSpan(span);
								newCE.setSpanIndex(j);
								replicationList.add(newCE);
							}
						}
					}
					removeList.add(ce);
				} else {
					registerExceptionThread(ce);
				}
			}

			// If there's anything on the "remove" list, get rid of it.
			removeEntries(condList, removeList);

			// replicate the tagged Span objects
			replicateEntries(condList, replicationList);

			// just so debug stuff - TODO: get rid of this
/*
			System.out.println(this + ": end of main loop");
			for (int i = 0; i < condList.size(); i++) {
				System.out.println(this + ": waiting on: "
						+ condList.get(i).getCurrentLink().toString());
			}
*/
		}

		// System.out.println(this + "job done");
	}
	
	private int runTasks(ChainEnvironment ce, EnvironmentList removeList, EnvironmentList replicationList) {
		// run through the rest of the list until you hit a Condition
		int state = ce.runTasks(this, msg);

		// if we're done, remove this from the work list.
		// if we've hit a Condition that has a Span, add it to the
		// replication list and register an ExceptionThread,
		// if there is one.
		if (state == ce.COMPLETED) {
			removeList.add(ce);
		} else if (state == ce.EXPAND_SPAN) {
			Link currentLink = ce.getCurrentLink();
			if (currentLink instanceof Condition) {
				Condition cond = (Condition) currentLink;
				Span span = cond.getSpan();

				if (span != null) {
					for (int j = span.lower; j <= span.upper; j = j
							+ span.increment) {
						ChainEnvironment newCE = ce.clone();
						newCE.setSpan(span);
						newCE.setSpanIndex(j);
						replicationList.add(newCE);
					}
				}
			}
			removeList.add(ce);
		} else {
			registerExceptionThread(ce);
		}
		return state;
	}
	
	private void removeEntries(EnvironmentList masterList, EnvironmentList removeList) {
		for (int i = 0; i < removeList.size(); i++) {
			ChainEnvironment ce = removeList.get(i);
			masterList.remove(ce);
			ce = null;
		}
		removeList.clear();
	}

	private void replicateEntries(EnvironmentList masterList, EnvironmentList replicationList) {
		// replicate the tagged Span objects
		for (int i = 0; i < replicationList.size(); i++) {
			ChainEnvironment ce = replicationList.get(i);
			masterList.add(ce);
			registerExceptionThread(ce);
		}
		replicationList.clear();
	}
	
	public void registerExceptionThread(ChainEnvironment ce) {
		Link link = ce.getCurrentLink();
		if (link instanceof Condition) {
			Condition cond = (Condition) link;
			if (cond.getTimeout() > 0L) {
				ExpireThread thread = new ExpireThread(this, cond, ce);
				ce.setExceptionThread(thread);
				thread.start();
			}
		}
	}

	// this looks like a stupid method, but it's here to make sure that the
	// threads accessing this and the getter method don't step on each other
	// and return inconsistent results
    /**
     * Setter method to set the current message for the EngineWorker.  
     * This is synchronized so multiple threads don't step on each other.
     */
	public synchronized MonitorMessage setCurrentMessage(MonitorMessage msg) {
		this.msg = msg;
		return msg;
	}

    /**
     * Accessor method to get  the current message from the EngineWorker.  
     * This is synchronized so multiple threads don't step on each other.
     */
	public synchronized MonitorMessage getCurrentMessage() {
		return msg;
	}

}
