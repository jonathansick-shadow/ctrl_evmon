package lsst.ctrl.evmon.output;

import lsst.ctrl.evmon.engine.EventStore;

public interface StringWriter {
	public void send(EventStore es, String query);
}
