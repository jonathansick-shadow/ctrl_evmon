package lsst.ctrl.evmon;

/**
 * This enum represents the different types of comparisons that can be
 * made in LogicalExpressions
 */
public enum Relation {
	EQUALS {
		boolean eval(String left, String right) {
			// System.out.println("left = "+left+", right ="+right);
			if (left == null)
				return false;
			return left.equals(right);
		}
	},
	NOT_EQUAL {
		boolean eval(String left, String right) {
			if (left == null)
				return false;
			if (left.equals(right) == false)
				return true;
			return false;
		}
	},
	GREATER_THAN {
		boolean eval(String left, String right) {
			Float leftSide = Float.valueOf(left);
			Float rightSide = Float.valueOf(right);
			if (leftSide > rightSide)
				return true;
			return false;
		}
	},
	LESS_THAN {
		boolean eval(String left, String right) {
			Float leftSide = Float.valueOf(left);
			Float rightSide = Float.valueOf(right);
			if (leftSide < rightSide)
				return true;
			return false;
		}
	},
	GREATER_OR_EQUAL {
		boolean eval(String left, String right) {
			Float leftSide = Float.valueOf(left);
			Float rightSide = Float.valueOf(right);
			if (leftSide >= rightSide)
				return true;
			return false;
		}
	},
	LESS_OR_EQUAL {
		boolean eval(String left, String right) {
			Float leftSide = Float.valueOf(left);
			Float rightSide = Float.valueOf(right);
			if (leftSide >= rightSide)
				return true;
			return false;
		}
	},
	STARTS_WITH {
		boolean eval(String left, String right) {
			try {
				if (left.startsWith(right))
					return true;
				return false;
			} catch (NullPointerException npe) {
				return false;
			}
		}
	},
	ENDS_WITH {
		boolean eval(String left, String right) {
			if (left.endsWith(right))
				return true;
			return false;
		}
	},
	CONTAINS {
		boolean eval(String left, String right) {
			if (left.contains(right))
				return true;
			return false;
		}
	};

	abstract boolean eval(String left, String right);
}
