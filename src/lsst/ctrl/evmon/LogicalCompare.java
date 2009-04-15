package lsst.ctrl.evmon;

import lsst.ctrl.evmon.engine.ChainEnvironment;
import lsst.ctrl.evmon.engine.EventStore;
import lsst.ctrl.evmon.engine.MonitorMessage;

public class LogicalCompare implements LogicalExpression {

	String key;
	Relation relation;
	Object value;
	
	public LogicalCompare(String key, Relation relation, Object value) {
		this.key = key;
		this.relation = relation;
		this.value = value;
	}

	// TODO: This needs to be reworked to avoid unnecessarily converting
	// a numeric value into a string.
	public boolean evaluate(ChainEnvironment ce, MonitorMessage msg) {
		String leftSide = null;
		String rightSide = null;

		EventStore es = ce.getEventStore();

		leftSide = lookup(es, msg, key);

		if (value.equals(Span.INDEX)) {
			rightSide = Integer.toString(ce.getSpanIndex());
		} else {
			rightSide = lookup(es, msg, value.toString());
			if (rightSide == null) {
				System.err.println("LogicalCompare: Error: \""+value.toString()+"\" does not exist");
				System.exit(100);
			}
		}

		return relation.eval(leftSide, rightSide);
	}
	
	private String lookup(EventStore es, MonitorMessage msg, String token) {
		if (token.startsWith("$msg:")) {
			String[] str = token.split(":");
			String val = null;
			Object obj = msg.get(str[1]);
			if (obj != null) {
				val = obj.toString();
			}
			return val;
		}
		return es.lookup(token);
	}
	
	public String toString() {
		return key + " " + relation + " " + value;
	}
}
