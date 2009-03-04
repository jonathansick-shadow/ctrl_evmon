package lsst.ctrl.evmon;

import lsst.ctrl.evmon.engine.EventStore;
import lsst.ctrl.evmon.engine.MonitorMessage;

public interface Task extends Link {
	
	public void execute(EventStore es, MonitorMessage event);

}
