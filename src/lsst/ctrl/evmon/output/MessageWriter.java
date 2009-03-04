package lsst.ctrl.evmon.output;

import lsst.ctrl.evmon.Template;
import lsst.ctrl.evmon.engine.EventStore;

public interface MessageWriter {
	public void send(EventStore es, Template template);
}
