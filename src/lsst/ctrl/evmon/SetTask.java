package lsst.ctrl.evmon;

import java.math.BigDecimal;
import java.util.StringTokenizer;

import lsst.ctrl.evmon.engine.EventStore;
import lsst.ctrl.evmon.engine.MonitorMessage;
import lsst.ctrl.evmon.math.ArithmeticExpression;


public class SetTask implements Task {
	String result;
	String expression;

	public SetTask(String result, String expression) {
		this.result = result;
		this.expression = expression;
	}

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
						val = (String) (msg.get(str[1])).toString();
					} else {
						MonitorMessage mm = (MonitorMessage) es.get(str[0]);
						val = (String) (mm.get(str[1])).toString();
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
			System.out.println("Evaluate: \"" + eval + "\"");
			ArithmeticExpression expr = new ArithmeticExpression(eval);
			BigDecimal val = expr.evaluate();
			System.out.println("evaluated as = " + val);
			es.put(result, val.toString());
		} else {
			es.put(result, eval);
		}
	}

	public String toString() {
		return result + " = " + expression;
	}
}