package lsst.ctrl.evmon;


public class TemplateEntry {

	int type;
	String key;
	Object value;


	public TemplateEntry(String key, int type, Object value) {
		this.key = key;
		this.type = type;
		this.value = value;

	}
	
	public String getKey() {
		return key;
	}
	
	public int getType() {
		return type;
	}
	
	public String getTypeString() {
		switch (type) {
		case Template.STRING:
			return "string";
		case Template.INT:
			return "int";
		case Template.DOUBLE:
			return "double";
		case Template.FLOAT:
			return "float";
		}
		return "unknown";
	}
	
	public Object getValue() {
		return value;
	}
}
