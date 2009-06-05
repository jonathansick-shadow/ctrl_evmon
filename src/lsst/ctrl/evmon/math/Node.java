package lsst.ctrl.evmon.math;

/**
 * Class Node represents the left-hand side of a mathematical expression, the
 *  operator, and the right-hand side of a mathematical expression.
 */
public class Node {
	final static int OP = 1;
	final static int NUM = 2;
	int type;
	Node left;
	Node right;
	String value;
}
