package lsst.ctrl.evmon;

import java.util.Vector;

import lsst.ctrl.evmon.engine.ChainEnvironment;
import lsst.ctrl.evmon.engine.MonitorMessage;


public class LogicalAnd extends Vector<LogicalExpression> implements LogicalExpression {
	
	public LogicalAnd(LogicalExpression expr1, LogicalExpression expr2) {
		add(expr1);
		add(expr2);
	}
	
	public boolean evaluate(ChainEnvironment ce, MonitorMessage msg) {
		int total = this.size();
		
		for (int i = 0; i < total; i++) {
			LogicalExpression expr = get(i);
			if (expr.evaluate(ce, msg) == false)
				return false;
		}
		return true;
	}
	
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
