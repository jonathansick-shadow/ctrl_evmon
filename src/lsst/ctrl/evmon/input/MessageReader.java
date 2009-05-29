package lsst.ctrl.evmon.input;

import lsst.ctrl.evmon.engine.MonitorMessage;

/**
 * Interface MessageReader describes the interface used to retrieve messages
 * from input streams.
public interface MessageReader {
    /**
     * Retrieve the next available message from the Reader's data source
     * @return MonitorMessage encapsulating the retrieved message
     */
	public MonitorMessage getMessage();
}
