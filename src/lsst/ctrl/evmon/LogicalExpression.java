package lsst.ctrl.evmon;

import lsst.ctrl.evmon.engine.ChainEnvironment;
import lsst.ctrl.evmon.engine.MonitorMessage;

public interface LogicalExpression {
	public boolean evaluate(ChainEnvironment ce, MonitorMessage msg);
}
