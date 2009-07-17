package lsst.ctrl.evmon;

import java.util.HashSet;
import java.util.Vector;

public class AttributeSet {
	HashSet<String>set = new HashSet<String>();
	
	public AttributeSet() {
	}
	
    /**
     * This menthod puts another string into the set
     * @param s the string to add to the set
     */
	public void put(String s) {
		set.add(s);
	}
	
    /**
     * This menthod puts determines whether a string is in the set
     * @param s the string to add to the set
     * @return true if the string is contained in the set, otherwise it returns false
     */
	public boolean contains(String s) {
		return set.contains(s);
	}
	                          
}
