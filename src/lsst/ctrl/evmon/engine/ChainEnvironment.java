package lsst.ctrl.evmon.engine;

import lsst.ctrl.evmon.Assertion;
import lsst.ctrl.evmon.Chain;
import lsst.ctrl.evmon.Condition;
import lsst.ctrl.evmon.Link;
import lsst.ctrl.evmon.Span;
import lsst.ctrl.evmon.Task;

public class ChainEnvironment  {
	
	public static final int COMPLETED = -2001;
	public static final int PENDING = -2002;
	public static final int EXPAND_SPAN = -2003;
	
	EventStore es = null;
	Chain chain = null;
	int currentLink = 0;
	int conditionalCounter = 0;
	int spanIndex = -1;
	Span span = null;
	boolean isExpired;
	ExpireThread exceptionThread = null;
	int state;
	
	public ChainEnvironment(Chain chain) {
		this.chain = chain;
		es = new EventStore();
		this.isExpired = false;
		this.state = PENDING;
	}
	
	public ChainEnvironment(Chain chain, EventStore es) {
		this.chain = chain;
		this.es = es;
	}
	
	public Chain getChain() {
		return chain;
	}
	
	public int getIndex() {
		return currentLink;
	}
	
	public Link getCurrentLink() {
		return chain.getLink(currentLink);
	}
	
	public int getSpanIndex() {
		return spanIndex;
	}
	
	public void setSpanIndex(int index) {
		es.put(Span.INDEX, index);
		this.spanIndex = index;
	}
	
	public void setSpan(Span span) {
		this.span = span;
	}
	
	public Span getSpan() {
		return span;
	}
	
	public EventStore getEventStore() {
		return es;
	}
	
	public boolean isExpired() {
		return isExpired;
	}
	
	public void setExpired(boolean b) {
		this.isExpired = b;
	}
	
	public void setExceptionThread(ExpireThread thread) {
		this.exceptionThread = thread;
	}
	
	// this looks like a stupid thing to have a method for, but
	// the EngineWorker uses it to update the currentLink it is
	// looking at.  This is used when calling an invocation of
	// runTasks() that happens after a conditional.   It used
	// to be embedded in runTasks, but if it's there, and a Task
	// is at the beginning of a Chain, the link gets incremented
	// improperly and the first Task is missed.
	public void updateChainPointer() {
		currentLink++;
	}
	
	public int runTasks(EngineWorker engine, MonitorMessage msg) {
		Link link = chain.getLink(currentLink);
		if (link == null) {
			state = COMPLETED;
			return state;
		}
		if (link instanceof Assertion) {
			Assertion assertion = (Assertion)link;
			if (assertion.evaluate(this, msg) != true) {
				assertion.execute(es);
				state = COMPLETED;
				return state;
			}
		}

		if (link instanceof Condition) {
			Condition cond = (Condition)link;
			if (cond.getSpan() != span) {
				state = EXPAND_SPAN;
				return state;
			}
			else {
				state = PENDING;
				return state;
			}
		}
		while (link instanceof Task) {
			Task task = (Task)link;
			task.execute(es,msg);
			link = chain.getLink(++currentLink);
			if (link == null) {
				state = COMPLETED;
				return state;
			}
		}
		state = PENDING;
		return state;
	}
	
	public int getState() {
		return state;
	}
	
	public void storeMessage(MonitorMessage msg) {
		es.put("$msg["+conditionalCounter+"]", msg);
		conditionalCounter++;
	}
	
	public boolean evaluate(MonitorMessage msg) {
		Link currentCondition = chain.getLink(currentLink);
		if (currentCondition instanceof Condition) {
			Condition cond = (Condition)currentCondition;
			boolean state = cond.evaluate(this, msg);
			return state;
		}
		
		// if nothing matches, something weird is happening...
		return false;
	}
	
	public ChainEnvironment clone() {
	
		ChainEnvironment cloned = new ChainEnvironment(chain, es.clone());
		cloned.currentLink = currentLink;
		cloned.conditionalCounter = conditionalCounter;
		cloned.currentLink = currentLink;
		cloned.span = span;
		
		return cloned;
	}
}
