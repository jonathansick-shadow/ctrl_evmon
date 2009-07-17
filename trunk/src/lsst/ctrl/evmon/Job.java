package lsst.ctrl.evmon;

import lsst.ctrl.evmon.input.MessageReader;



/**
 * Class Job holds the event input source and event monitor Chain which will receive those events.  The Job object is used by
 * the EventMonitor object to store this information.
 */
public class Job {
	MessageReader input = null;

	Chain chain = null;
	
    /**
     * Initializes a new Job object with an input source and Chain
     *
     * @param input a MessageReader object representing the input source
     * @param chain a Chain which will receive events from the MessageReader
     */
	public Job(MessageReader input, Chain chain) {
		this.input = input;
		this.chain = chain;
	}
	
    /**
     * Accessor method to retrieve the MessageReader for this Job
     * @return the MessageReader for this Job.
     */
	public MessageReader getReader() {
		return input;
	}

    /**
     * Accessor method to retrieve the Chain for this Job
     * @return the Chain for this Job.
     */
	public Chain getChain() {
		return chain;
	}
	
	
    /** 
     * The String representation of the MessageReader
     * @return a string representing the MessageReader
     */
	public String toString() {
		return "job input = "+input.toString();
	}
}
