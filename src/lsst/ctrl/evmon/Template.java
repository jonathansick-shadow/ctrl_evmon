package lsst.ctrl.evmon;

import java.util.Vector;


public class Template {
	public static final int STRING = 1;
	public static final int INT = 2;
	public static final int DOUBLE = 3;
	public static final int FLOAT = 4;
	Vector<TemplateEntry> vec = new Vector<TemplateEntry>();
	
	public Template() {
	}
	
	public void put(String key, int type, Object value) {
		vec.add(new TemplateEntry(key, type, value));
	}

	public void put (TemplateEntry entry) {
		vec.add(entry);
	}
	
	public TemplateEntry get(int index) {
		return vec.get(index);
	}

	public int size() {
		return vec.size();
	}
	
}