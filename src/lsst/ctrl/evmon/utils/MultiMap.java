package lsst.ctrl.evmon.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class MultiMap {
	Map<String, List<Object>> map = null;

	public MultiMap() {
		map = new HashMap<String, List<Object>>();
	}
	
	// The following series of "put" methods might seem redundant, but it is
	// in place to enforce retaining the types of each of the objects so they
	// can be serialized with type information later.
	
	public void put(String key, Object value) {
		List<Object> l = map.get(key);
		if (l == null)
			map.put(key, l = new ArrayList<Object>());
		l.add(value);
	}

	
	public void put(String key, int value) {
		put(key, new Integer(value));
	}
	
	public void put(String key, float value) {
		put(key, new Float(value));
	}
	
	public void put(String key, double value) {
		put(key, new Double(value));
	}
	
	public void put(String key, long value) {
		put(key, new Long(value));
	}
	
	public Set entrySet() {
		return map.entrySet();
	}
	
	public Set keySet() {
		return map.keySet();
	}

	public Collection getAll(String key) {
		return map.get(key);
	}

	public Object get(String key) {
		List l = map.get(key);
		if (l == null)
			return null;
		return l.get(0);
	}
	
	public List getList(String key) {
		List l = map.get(key);
		return l;
	}
	
	public MultiMap clone() {
		MultiMap clonedMap = new MultiMap();
		Set keys = keySet();
		Iterator it = keys.iterator();
		while (it.hasNext()) {
			String key = (String)it.next();
			List list = getList(key);
			for (int i = 0; i < list.size(); i++) {
				clonedMap.put(key, list.get(i));
			}
		}
		return clonedMap;
	}
}

