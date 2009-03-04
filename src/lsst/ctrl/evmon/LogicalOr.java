package lsst.ctrl.evmon;

import java.util.Vector;

import lsst.ctrl.evmon.engine.ChainEnvironment;
import lsst.ctrl.evmon.engine.MonitorMessage;


public class LogicalOr extends Vector<LogicalExpression> implements LogicalExpression {
	LogicalExpression expr1 = null;
	LogicalExpression expr2 = null;
	
	public LogicalOr(LogicalExpression expr1, LogicalExpression expr2) {
		add(expr1);
		add(expr2);
	}

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