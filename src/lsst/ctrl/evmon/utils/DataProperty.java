package lsst.ctrl.evmon.utils;

public class DataProperty implements DataPropertyType {
	String key;
	Object value;
	
	public DataProperty(String key, Object value) {
		this.key = key;
		this.value = value;
	}
	
	public String getKey() {
		return key;
	}
	
	public Object getValue() {
		return value;
	}
}