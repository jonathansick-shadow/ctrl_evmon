package lsst.ctrl.evmon.output;

import lsst.ctrl.evmon.Template;
import lsst.ctrl.evmon.engine.EventStore;

/**
 * Interface MessageWriter describes the methods needed to write messages.
 */
public interface MessageWriter {
    /**
     * Generic "send" method takes an EventStore, which is used to lookup stored variables
     * and a Template, which describes what to put in the message.
     * @param es The EventStore to use to lookup variables.
     * @param template The key/value pairs to send
     */
	public void send(EventStore es, Template template);
}
