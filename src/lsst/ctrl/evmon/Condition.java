package lsst.ctrl.evmon;

import java.util.Vector;

import lsst.ctrl.evmon.engine.ChainEnvironment;
import lsst.ctrl.evmon.engine.MonitorMessage;


/**
 * The <code>Condition</code> class represents the encapsulation of a
 * logical expression which can be evaluated by the EventMonitor.
 * 
 * @author srp
 *
 */
public class Condition implements Link {
	Span span = null;
	LogicalExpression expression = null;
	
	ExceptionTask exceptionTask = null;
	long timeout = 0;

	/**
	 * Class constructor which initializes a <code>Condition</code> object with the arguments for 
	 * a LogicalCompare.
	 * 
	 * @param key the variable to compare
	 * @param relation describes the relationship to use to evaluate key against value
	 * @param value the value to use to compare against the key
	 */
	public Condition(String key, Relation relation, Object value) {
		this(new LogicalCompare(key, relation, value));
	}
	
	/**
	 * Class constructor which initializes a <code>Condition</code> object with a LogicalExpression.
	 * 
	 * @param e the LogicalExpression to evaluate.
	 */
	public Condition(LogicalExpression e) {
		this(e,null);
	}
	
	/**
	 * Class constructor which initializes a <code>Condition</code> object with a LogicalExpression,
	 * along with a Span.
	 * 
	 * @param e the LogicalExpression to evaluate.
	 * @param s the span to use for indexing purposes.
	 */
	public Condition(LogicalExpression e, Span s) {
		this.expression = e;
		this.span = s;
	}
	
    /**
     * Sets an ExceptionTask and timeout for this Condition.  If this condition is not
     * met within the specified timeout period, the ExceptionTask is executed, and the
     * rest of the Links on this Chain will not be executed.
     * 
     * @param task the ExceptionTask to execute on timeout
     * @param timeout the number of milliseconds to wait.
     */
	public void setException(ExceptionTask task, long timeout) {
		this.exceptionTask = task;
		this.timeout = timeout;
	}

    /**
     * Returns the Span associated with this Condition (if any).  If a Span does exist,
     * it is resolved, and returned.
     */
	public Span getSpan() {
		if (span == null)
			return null;
		span.resolve();
		return span;
	}
	
	// is this still needed?
	public ExceptionTask getExceptionTask() {
		return exceptionTask.clone();
	}

    /**
     * Accessor method that returns the timeout associated with this Condition
     * @return timeout value in milliseconds
     */
	public long getTimeout() {
		return timeout;
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
     * Returns a string representation of this object's LogicalExpression, in unevaluated form.
     * @return a string representation of this object's Logical Expression
     */
	public String toString() {
		return expression.toString();
	}
}
