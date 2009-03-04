package lsst.ctrl.evmon.utils;

import java.util.LinkedList;

public class DataPropertyNode extends LinkedList implements DataPropertyType {
	String name;

	
	public DataPropertyNode(String name) {
		this.name = name;
	}	
}