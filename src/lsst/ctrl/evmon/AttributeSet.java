package lsst.ctrl.evmon;

import java.util.HashSet;
import java.util.Vector;

public class AttributeSet {
	HashSet<String>set = new HashSet<String>();
	
	public AttributeSet() {
	}
	
	public void put(String s) {
		set.add(s);
	}
	
	public boolean contains(String s) {
		return set.contains(s);
	}
	                          
}
