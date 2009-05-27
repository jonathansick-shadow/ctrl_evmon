package lsst.ctrl.evmon;


/**
 * Class TemplateEntry specifies a key, its type, and value.
 */
public class TemplateEntry {

	int type;
	String key;
	Object value;

    /**
     * Class constructor TemplateEntry specifies a key, its type, and value.
     * @param key String representing a key
     * @param type Template constant representing the type of the key
     * @param value Object representing the value to store
     */
	public TemplateEntry(String key, int type, Object value) {
		this.key = key;
		this.type = type;
		this.value = value;

	}
	
    /**
     * Accessor method to return the key for this object
     * @return the String value which specifies the key
     */
	public String getKey() {
		return key;
	}
	
    /**
     * Accessor method to return the type for this object
     * @return Template constant of the key/value pair
     */
	public int getType() {
		return type;
	}
	
    /**
     * Returns the String representation of the Template type.
     * @return String of the Template type.
     */
    // TODO: move this to Template where it really belongs
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
	
    /**
     * Accessor method that returns the value
     * @return Object representing the "value
     */
	public Object getValue() {
		return value;
	}
}
