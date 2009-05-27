package lsst.ctrl.evmon;

import java.math.BigDecimal;

import lsst.ctrl.evmon.math.ArithmeticExpression;


/**
 * Class Span represents a group of numbers from a low to a high value
 */
public class Span {
	private Object low;
	private Object up;
	boolean resolved;
	public int lower;
	public int upper;
	public int increment = 1;
	
	static public String INDEX = "$SpanIndex";

    /**
     * Class constructor Span stores a lower and an upper value.
     */
	public Span(Object lower, Object upper) {
		this.low = lower;
		this.up = upper;
		
		resolved = false;
	}
	
    /**
     * This resolves the values of the lower and upper expressions, by evaluating
     * them when this method is called.  Currently this only evaluates numbers and
     * arithmetic expressions, and does not evaluate expressions with variables.
     */
	public void resolve() {
		if (resolved)
			return;
		ArithmeticExpression expr = new ArithmeticExpression(low.toString());
		BigDecimal intVal = expr.evaluate();
		this.lower = intVal.intValue();

		ArithmeticExpression expr2 = new ArithmeticExpression(up.toString());
		BigDecimal intVal2 = expr2.evaluate();
		this.upper = intVal2.intValue();
	}
}
