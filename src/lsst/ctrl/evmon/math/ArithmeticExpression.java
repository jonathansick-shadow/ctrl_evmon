package lsst.ctrl.evmon.math;

import java.math.BigDecimal;
import java.util.StringTokenizer;

public class ArithmeticExpression {
	Node root = null;

	public enum Expecting {
		NUMBER, OP,
	};

	public static void main(String[] args) {

		ArithmeticExpression e0 = new ArithmeticExpression("-12-2");
		BigDecimal s0 = e0.evaluate(e0.root);
		System.out.println("here = " + s0);

		ArithmeticExpression e1 = new ArithmeticExpression("1.1");
		BigDecimal s1 = e1.evaluate(e1.root);
		System.out.println("here = " + s1);

		ArithmeticExpression e2 = new ArithmeticExpression("1.2+1");
		BigDecimal s2 = e2.evaluate(e2.root);
		System.out.println("here = " + s2);

		ArithmeticExpression e3 = new ArithmeticExpression("2+1.3");
		BigDecimal s3 = e3.evaluate(e3.root);
		System.out.println("here = " + s3);

		ArithmeticExpression e4 = new ArithmeticExpression("1.1+1.1+ 2.2");
		BigDecimal s4 = e4.evaluate(e4.root);
		System.out.println("here = " + s4);

		ArithmeticExpression e5 = new ArithmeticExpression("20-12");
		BigDecimal s5 = e5.evaluate(e5.root);
		System.out.println("here = " + s5);
	}

	public ArithmeticExpression(String input) {
		root = parse(input);
	}

	public BigDecimal evaluate() {
		return evaluate(root);
	}

	public BigDecimal evaluate(Node n) {
		BigDecimal left = new BigDecimal(0);
		BigDecimal right = new BigDecimal(0);
		if (n.left != null) {
			left = evaluate(n.left);
		}
		if (n.right != null) {
			right = evaluate(n.right);
		}
		if (n.type == Node.NUM)
			return new BigDecimal(n.value);
		char op = n.value.charAt(0);
		switch (op) {
		case '+':
			return left.add(right);
		case '-':
			return left.subtract(right);
		case '*':
			return left.multiply(right);
		case '/':
			return left.divide(right);
		}
		return new BigDecimal(0);
	}

	public Node parse(String input) {
		Expecting state = Expecting.NUMBER;
		StringTokenizer st = new StringTokenizer(input, "+-*/", true);
		Node node = null;
		Node op = null;
		boolean first = true;

		while (st.hasMoreTokens()) {
			String s = st.nextToken().trim();
			if (s.length() == 0)
				continue;


			try {
				if (s.equals("-") && (first == true)) {
					s += st.nextToken().trim();
				}
				first = false;
				BigDecimal value = new BigDecimal(s);
				if (state != Expecting.NUMBER) {
					System.out.println("Bad expression \"" + input + "\"");
					return null;
				}
				node = new Node();
				node.type = Node.NUM;
				node.value = s;
				if (op != null) {
					op.right = node;
				}
				state = Expecting.OP;
			} catch (NumberFormatException nfe) {
				if (s.length() != 1) {
					System.out.println("Bad expression \"" + input + "\"");
					return null;
				}
				char c = s.charAt(0);
				switch (c) {
				case '+':
				case '-':
				case '/':
				case '*':
					if (state == Expecting.NUMBER) {
						System.out.println("Bad expression \"" + input + "\"");
						return null;
					}
					if (op != null) {
						Node newop = new Node();
						newop.left = op;
						newop.type = Node.OP;
						newop.value = s;
						op = newop;
					} else {
						op = new Node();
						op.value = s;
						op.type = Node.OP;
						op.left = node;
					}
					state = Expecting.NUMBER;
					break;
				}
			}
		}
		if (op == null)
			return node;
		return op;
	}
}