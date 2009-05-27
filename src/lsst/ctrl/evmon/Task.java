package lsst.ctrl.evmon;

import lsst.ctrl.evmon.engine.EventStore;
import lsst.ctrl.evmon.engine.MonitorMessage;

/**
 * Interface Task specifies method signatures for Chain Links that do jobs
 * (such as writing data, storing information, etc).
 */
public interface Task extends Link {
	
    /**
     * Method to execute when this Link is encountered in a Chain by the EventMonitor
     */
	public void execute(EventStore es, MonitorMessage event);

}
