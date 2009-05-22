package lsst.ctrl.evmon;

import java.util.Vector;

import lsst.ctrl.evmon.engine.ChainEnvironment;
import lsst.ctrl.evmon.engine.MonitorMessage;


/**
 * Class LogicalAnd represents a group of two or more LogicalExpressions.
 */
public class LogicalAnd extends Vector<LogicalExpression> implements LogicalExpression {
	
    /**
     * Class constructor LogicalAnd stores two LogicalExpressions for evaluation later.
     */
	public LogicalAnd(LogicalExpression expr1, LogicalExpression expr2) {
		add(expr1);
		add(expr2);
	}
  
	/**
     * Evaluates all LogicalExpressions for this object
     * @return true, if all LogicalExpressions evaluate to true, otherwise returns false
     */
	public boolean evaluate(ChainEnvironment ce, MonitorMessage msg) {
		int total = this.size();
		
		for (int i = 0; i < total; i++) {
			LogicalExpression expr = get(i);
			if (expr.evaluate(ce, msg) == false)
				return false;
		}
		return true;
	}
	
    /**
     * Returns a string showing all the LogicalExpressions held by this object.
     * @return a string representation of all this object's LogicalExpressions.
     */
	public String toString() {
		String s = "";
		int total = this.size();
		for (int i = 0; i < total; i++) {
				LogicalExpression expr = get(i);
				s += expr + " ";
		}
		return s;
	}
}
