package lsst.ctrl.evmon.input;

import lsst.ctrl.evmon.engine.MonitorMessage;

public interface MessageReader {
	public MonitorMessage getMessage();
}
