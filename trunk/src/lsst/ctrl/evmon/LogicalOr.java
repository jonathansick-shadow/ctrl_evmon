package lsst.ctrl.evmon;

import java.util.Vector;

import lsst.ctrl.evmon.engine.ChainEnvironment;
import lsst.ctrl.evmon.engine.MonitorMessage;

/**
 * Class LogicalOr represents a group of two LogicalExpressions, which when
 * evaluated will return true if any of the LogicalExpressions are true.
 */
public class LogicalOr extends Vector<LogicalExpression> implements LogicalExpression {
	LogicalExpression expr1 = null;
	LogicalExpression expr2 = null;
	
    /**
     * Class constructor LogicalOr stores two LogicalExpressions for evaluation later. 
     * @param expr1 first LogicalExpression
     * @param expr2 second LogicalExpression
     */
	public LogicalOr(LogicalExpression expr1, LogicalExpression expr2) {
		add(expr1);
		add(expr2);
	}

    /**
     * Evaluates each LogicalExpressions for this object.
     * @param ce ChainEnvironment to evaluate against
     * @param msg current message to evaluate against
     * @return true if at least one LogicalExpressions evaluate to true. If none evaulate to true, this method returns false
     */
	public boolean evaluate(ChainEnvironment ce, MonitorMessage msg) {
		int total = this.size();
		
		for (int i = 0; i < total; i++) {
			LogicalExpression expr = get(i);
			if (expr.evaluate(ce, msg) == true)
				return true;
		}
		return false;
	}
}
