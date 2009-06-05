package lsst.ctrl.evmon.output;

import lsst.ctrl.evmon.engine.EventStore;

/**
 * Interface StringWriter sends a string which has been resolved using an EventStore
 */
public interface StringWriter {
    /**
     * Sends a string which has been resolved using an EventStore
     * @param es the EventStore to use to look up variables
     * @param s the String to send
     */
	public void send(EventStore es, String s);
}
