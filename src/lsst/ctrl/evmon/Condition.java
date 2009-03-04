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
	 * Initializes a <code>Condition</code> object with the arguments for 
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
	 * Initializes a <code>Condition</code> object with a LogicalExpression,
	 * along with a Span.
	 * 
	 * @param e the LogicalExpression to evaluate.
	 */
	public Condition(LogicalExpression e) {
		this.expression = e;
	}
	
	public Condition(LogicalExpression e, Span s) {
		this.expression = e;
		this.span = s;
	}
	
	public void setException(ExceptionTask task, long timeout) {
		this.exceptionTask = task;
		this.timeout = timeout;
	}

	public Span getSpan() {
		span.resolve();
		return span;
	}
	
	// is this still needed?
	public ExceptionTask getExceptionTask() {
		return exceptionTask.clone();
	}

	public long getTimeout() {
		return timeout;
	}

	public boolean evaluate(ChainEnvironment ce, MonitorMessage msg) {
		return expression.evaluate(ce, msg);
	}

	public String toString() {
		return expression.toString();
	}
}