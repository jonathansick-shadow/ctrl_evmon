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

    /**
     * Class constructor which stores the logical expression to be evaluated and the task to execute if assertion fails
     *
     * @param expression the LogicalExpression to be evaluated
     * @param task the ExceptionTask to execute, if the LogicalExpression returns false when it is evaluated
     *
     */
	public Assertion(LogicalExpression expression, ExceptionTask task) {
		this.expression = expression;
		this.task = task;
	}
	
    /**
     * Returns boolean result of evaluating the constructor's LogicalExpression, based on values in the ChainEnvironment and
     * MonitorMessage.
     *
     * @param ce The ChainEnvironment to use to evaluate the LogicalExpression
     * @param msg The message that is currently being evaluated
     * @return returns true if expression evaluates to true, false otherwise
     */
	public boolean evaluate(ChainEnvironment ce, MonitorMessage msg) {
		return expression.evaluate(ce, msg);
	}
	
    /**
     * Perform the constructor's ExceptionTask, using the EventStore, if required.
     *
     * @param es The EventStore to use when executing the ExceptionTask
     */
	public void execute(EventStore es) {
		task.execute(es);
	}

}
