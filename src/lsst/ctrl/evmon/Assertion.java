package lsst.ctrl.evmon;

import lsst.ctrl.evmon.engine.ChainEnvironment;
import lsst.ctrl.evmon.engine.EventStore;
import lsst.ctrl.evmon.engine.MonitorMessage;

/**
 * The <code>Assertion</code> class represents a logical expression, and an
 * exception to execute if the logical expression evaluates to "false".
 * 
 * @author srp
 *
 */
public class Assertion implements Link {
	
	LogicalExpression expression = null;
	ExceptionTask task;

	public Assertion(LogicalExpression expression, ExceptionTask task) {
		this.expression = expression;
		this.task = task;
	}
	
	public boolean evaluate(ChainEnvironment ce, MonitorMessage msg) {
		return expression.evaluate(ce, msg);
	}
	
	public void execute(EventStore es) {
		task.execute(es);
	}

}
