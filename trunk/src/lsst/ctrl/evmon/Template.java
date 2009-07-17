package lsst.ctrl.evmon;

import java.util.Vector;


/**
 * Class Template represents an outline of how a message is represented.  This is
 * used by various other objects to specify how the key/value pairs will be stored
 * or written.
 */
public class Template {
	public static final int STRING = 1;
	public static final int INT = 2;
	public static final int DOUBLE = 3;
	public static final int FLOAT = 4;
	Vector<TemplateEntry> vec = new Vector<TemplateEntry>();
	
    /**
     * Class constructor for Template.  The template is initially "blank". Entries
     * in the Template are retrieved as TemplateEntry objects.
     */
	public Template() {
	}
	
    /**
     * Put a key/value part, and type, into this Template
     *
     * @param key String representing the key name
     * @param type Template constant representing the key/value pair's type
     * @param value Object representing the value of this key
     */
	public void put(String key, int type, Object value) {
		vec.add(new TemplateEntry(key, type, value));
	}

    /**
     * Put a TemplateEntry into this Template
     *
     * @param entry TemplateEntry to store
     */
	public void put (TemplateEntry entry) {
		vec.add(entry);
	}
	
    /**
     * Accessor method to retrieve the TemplateEntry at an index.
     * @return TemplateEntry at this index
     */
	public TemplateEntry get(int index) {
		return vec.get(index);
	}

    /**
     * Method to return the number of entries in the Template
     * @return the size of the Template.
     */
	public int size() {
		return vec.size();
	}
	
}
