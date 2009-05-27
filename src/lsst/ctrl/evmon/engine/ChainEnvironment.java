package lsst.ctrl.evmon.engine;

import lsst.ctrl.evmon.Assertion;
import lsst.ctrl.evmon.Chain;
import lsst.ctrl.evmon.Condition;
import lsst.ctrl.evmon.Link;
import lsst.ctrl.evmon.Span;
import lsst.ctrl.evmon.Task;

/**
 * Class ChainEnvironment represents the current state of a particular Chain
 * which is being executed by the EventMonitor.  This includes keeping track
 * of things like Spans, exception threads, which link in the Chain is currently
 * being worked with, etc.
 */
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
	
    /**
     * Class constructor ChainEnvironment is initialized with a Chain, and a null
     * EventStore
     * @param Chain Chain to keep information about.
     */
	public ChainEnvironment(Chain chain) {
		this.chain = chain;
		es = new EventStore();
		this.isExpired = false;
		this.state = PENDING;
	}
	
    /**
     * Class constructor ChainEnvironment is initialized with a Chain, and an
     * EventStore
     * @param Chain Chain to keep information about.
     * @param es EventStore to use for this ChainEnvironment
     */
	public ChainEnvironment(Chain chain, EventStore es) {
		this.chain = chain;
		this.es = es;
	}
	
    /**
     * Accessor method to retrieve the ChainEnvironment's Chain
     * @return Chain that is used for this ChainEnvironment
     */
	public Chain getChain() {
		return chain;
	}
	
    /**
     * Accessor method to the index of the current Link being worked on
     * @return an integer representing the current Link in this Chain
     */
	public int getIndex() {
		return currentLink;
	}
	
    /**
     * Accessor method to retrieve the current Link being worked on
     * @return a Link object representing the current Link in this Chain
     */
	public Link getCurrentLink() {
		return chain.getLink(currentLink);
	}
	
    /**
     * Accessor method to retrieve the Span index used for this Chain.
     * @return an integer representing the Span index value for this particular ChainEnvironment
     */
	public int getSpanIndex() {
		return spanIndex;
	}
	
    /**
     * Setter method to store the Span index for this ChainEnvironment
     */
	public void setSpanIndex(int index) {
		es.put(Span.INDEX, index);
		this.spanIndex = index;
	}
	
    /**
     * Setter method to set a Span for this ChainEnvironment
     */
	public void setSpan(Span span) {
		this.span = span;
	}
	
    /**
     * Accessor method to retrieve the Span for this ChainEnvironment
     * @return the Span set for this ChainEnvironment
     */
	public Span getSpan() {
		return span;
	}
	
    /**
     * Accessor method to retrieve the EventStore for this ChainEnvironment
     * @return the EventStore for this ChainEnvironment
     */
	public EventStore getEventStore() {
		return es;
	}
	
    /**
     * Returns whether or not ChainEnvironment is still being used or not
     * @return true if this Chain and ChainEnvironment are completed, false otherwise
     */
	public boolean isExpired() {
		return isExpired;
	}
	
    /**
     * Setter method to set the expiration status of this ChainEnvironment
     * @param b the status of whether or not this ChainEnvironment has expired.
     */
	public void setExpired(boolean b) {
		this.isExpired = b;
	}
	
    /**
     * Setter method to set an ExpireThread for this ChainEnvironment
     * @param thread an ExpireThread
     */
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
    /**
     * Updates the ChainEnvironment's index into the Chain by one Link
     */
	public void updateChainPointer() {
		currentLink++;
	}
	
    /**
     * Performs an operation on the current Link in the Chain.  This can
     * be checking an Assertion, checking a Condition, or executing a task
     * @param ce ChainEnvironment to evaluate against
     * @param msg current message to evaluate against
     */
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
	
    /** 
     * Accessor method to retrieve the current state of the ChainEnvironment
     * @return the state of this environment, PENDING, COMPLETED, EXPAND_SPAN
     */
	public int getState() {
		return state;
	}
	
    /**
     * Method to store a MonitorMessage.  This is store as a function of the
     * Conditional which allowed the Chain to progress to the next link.
     */
	public void storeMessage(MonitorMessage msg) {
		es.put("$msg["+conditionalCounter+"]", msg);
		conditionalCounter++;
	}
	
    /**
     * Evaluates a MonitorMessage againts the current conditional
     * @return true, if the condition is evaluated to true, otherwise false
     */
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
	
    /**
     * Duplicates this object
     * @return a duplicate of this ChainEnvironment
     */
	public ChainEnvironment clone() {
	
		ChainEnvironment cloned = new ChainEnvironment(chain, es.clone());
		cloned.currentLink = currentLink;
		cloned.conditionalCounter = conditionalCounter;
		cloned.currentLink = currentLink;
		cloned.span = span;
		
		return cloned;
	}
}
