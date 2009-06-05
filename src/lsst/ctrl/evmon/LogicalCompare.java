package lsst.ctrl.evmon;

import lsst.ctrl.evmon.engine.ChainEnvironment;
import lsst.ctrl.evmon.engine.EventStore;
import lsst.ctrl.evmon.engine.MonitorMessage;

public class LogicalCompare implements LogicalExpression {

	String key;
	Relation relation;
	Object value;
	
    /**
     * Class constructor to compare a variable value to an object.   
     * @param key Used to look up values in the local variable store, or by using the message that is currently being operated on. 
     * @param relation The boolean operation to use when the comparison takes place
     * @param value The value to compare against.  This can be a primitive value, a variable in the local store, or in the message
     * that is currently being operated on.
     */
	public LogicalCompare(String key, Relation relation, Object value) {
		this.key = key;
		this.relation = relation;
		this.value = value;
	}

	// TODO: This needs to be reworked to avoid unnecessarily converting
	// a numeric value into a string.
    /**
     * Evalute the current message
     * @param ce ChainEnvironment to evaluate against
     * @param msg current message to evaluate against
     * @returns true  if evaluation of comparison is true, otherwise returns false
     */
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
	
    /*
     * lookup attemps to retrieve the value of token, using the EventStore
     * or message.
     */
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
	
    /*
     * String representation of this object.
     * @return the string value of this LogicalCompare object
     */
	public String toString() {
		return key + " " + relation + " " + value;
	}
}
