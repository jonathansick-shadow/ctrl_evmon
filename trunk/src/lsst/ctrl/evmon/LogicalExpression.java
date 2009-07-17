package lsst.ctrl.evmon;

import lsst.ctrl.evmon.engine.ChainEnvironment;
import lsst.ctrl.evmon.engine.MonitorMessage;

/**
 * Interface LogicalExpression specifies operations that are common to all
 * Expression types.  This mechanism is used by the EventMonitor so that
 * all expression types have a common interface.
 */
public interface LogicalExpression {
    /**
     * Evaluates LogicalExpression for this object
     * @param ce ChainEnvironment to evaluate against
     * @param msg current message to evaluate against
     * @return appropriate boolean, depending on the type of Expression which is being evaulated
     */
	public boolean evaluate(ChainEnvironment ce, MonitorMessage msg);
}
