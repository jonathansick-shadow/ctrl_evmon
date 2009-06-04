package lsst.ctrl.evmon;

import java.math.BigDecimal;
import java.util.StringTokenizer;

import lsst.ctrl.evmon.engine.EventStore;
import lsst.ctrl.evmon.engine.MonitorMessage;
import lsst.ctrl.evmon.math.ArithmeticExpression;


/**
 * Class SetTask assigns a value to a variable, which can be retrieved later by
 * other Links in a Chain.
 */
public class SetTask implements Task {
	String result;
	String expression;

    /**
     * Class constructor SetTask stores the expression to be evaluated in result.
     * This occurs when SetTask is encountered in the Chain by the EventMonitor.
     */
	public SetTask(String result, String expression) {
		this.result = result;
		this.expression = expression;
	}

    /**
     * Executes the SetTask.  This evaluates the expression, looking up
     * any values that need to be evaluated and preforming a simple math
     * expression evaluation if specified.   The result is stored in a
     * variable specified in the "result" string in the constructor, so it can
     * be looked up by another Link further down the chain.
     *
     * @param es EventStore to evaluate against
     * @param msg current message to evaluate against
     */
	public void execute(EventStore es, MonitorMessage msg) {
		// EventStore es = EventStore.getThreadInstance();
		String eval = "";
		StringTokenizer tokenizer = new StringTokenizer(expression, "+-/*()",
				true);
		int tokenCount = 0;
		while (tokenizer.hasMoreTokens()) {
			tokenCount++;
			String token = tokenizer.nextToken().trim();
			String val = token;
			if (token.charAt(0) == '$') {
				if (token.contains(":")) {
					// look up a message object
					String[] str = token.split(":");
					if (str[0].equals("$msg")) {
						Object obj = msg.get(str[1]);
						if (obj == null)
							val = null;
						else
							val = obj.toString();
					} else {
						MonitorMessage mm = (MonitorMessage) es.get(str[0]);
						Object obj = mm.get(str[1]);
						if (obj == null)
							val = null;
						else
							val = obj.toString();
					}
				} else {
					val = (String) es.get(token);
				}
			}
			eval += val;
		}
		// if there's more than one token, then we've got a math equation
		// so we have to evaluate it;  if there's only one token, just store
		// the result.
		if (tokenCount > 1) {
			// System.out.println("Evaluate: \"" + eval + "\"");
			ArithmeticExpression expr = new ArithmeticExpression(eval);
			BigDecimal val = expr.evaluate();
			// System.out.println("evaluated as = " + val);
			es.put(result, val.toString());
		} else {
			es.put(result, eval);
		}
	}

    /**
     * Returns a String representation of the result and expression.  This is
     * the "raw" form, as given to the constructor, not the evaluated form.
     * @return a String representation of "result" and "expression".
     */
	public String toString() {
		return result + " = " + expression;
	}
}
