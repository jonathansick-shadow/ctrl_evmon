package lsst.ctrl.evmon;

import java.math.BigDecimal;

import lsst.ctrl.evmon.math.ArithmeticExpression;


public class Span {
	private Object low;
	private Object up;
	boolean resolved;
	public int lower;
	public int upper;
	public int increment = 1;
	
	static public String INDEX = "$SpanIndex";

	public Span(Object lower, Object upper) {
		this.low = lower;
		this.up = upper;
		
		resolved = false;
	}
	
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
