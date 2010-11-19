package lsst.ctrl.evmon.engine;

import java.text.SimpleDateFormat;
import java.util.BitSet;
import java.util.Date;

import lsst.ctrl.evmon.utils.MultiMap;


/**
 * Class EventStore maintains key/values pairs.  The EventStore can contain
 * duplicate key names, with different values.
 */
public class EventStore {
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private MultiMap map = new MultiMap();
	
    /**
     * Class constructor
     */
	public EventStore() {
		
	}
	
    /**
     * Class constructor which initializes the EventStore with the contents
     * of a MultiMap.
     */
	public EventStore(MultiMap map) {
		this.map = map;
	}

    /**
     * Accessor method to retrieve the EventStore's Multimap
     * @return the Multimap for this EventStore
     */
	public MultiMap getMap() {
		return map;
	}
	
    /**
     * Determine whether or not the key exists in this EventStore
     * @param key the key to look up.
     * @return true if the key exists in this EventStore
     */
	public boolean exists(String key) {
		if (map.get(key) != null)
			return true;
		return false;
	}
	
    /**
     * Accessor method to retrieve an object
     * @param key String to use to look up a value
     * @return the object associated with this key, null if key does not exist.
     */
	public Object get(String key) {
		return map.get(key);
	}
	
    /**
     * Setter method to store a string value with a key to look it up later.
     * @param key String to use as a key
     * @param value String value to store
     */
	public void put(String key, String value) {
		map.put(key, value);
	}

    /**
     * Setter method to store an integer with a key to look it up later.
     * @param key String to use as a key
     * @param value integer value to store
     */
	public void put(String key, int value) {
		map.put(key, value);
	}
	
    /**
     * Setter method to store a BitSet with a key to look it up later.
     * @param key String to use as a key
     * @param value BitSet value to store
     */
	public void put(String key, BitSet value) {
		map.put(key, value);
	}
	
    /**
     * Setter method to store a MonitorMessage with a key to look it up later.
     * @param key String to use as a key
     * @param msg MonitorMessage value to store
     */
	public void put(String key, MonitorMessage msg) {
		map.put(key, msg);
	}

    /**
     * Setter method to replace a string value with a key that may already exist.
     * @param key String to use as a key
     * @param value String value to store
     */
	public void replace(String key, String value) {
		map.replace(key, value);
	}

    /**
     * Setter method to replace an integer with a key that may already exist.
     * @param key String to use as a key
     * @param value integer value to store
     */
	public void replace(String key, int value) {
		map.replace(key, value);
	}
	
    /**
     * Setter method to replace a BitSet with a key that may already exist..
     * @param key String to use as a key
     * @param value BitSet value to store
     */
	public void replace(String key, BitSet value) {
		map.replace(key, value);
	}
	
    /**
     * Setter method to replace a MonitorMessage with that may already exist.
     * @param key String to use as a key
     * @param msg MonitorMessage value to store
     */
	public void replace(String key, MonitorMessage msg) {
		map.replace(key, msg);
	}
	
	
    /**
     * Create a duplicate of this object
     * @return a clone of this object
     */
	public EventStore clone() {
		return new EventStore(map.clone());
	}
	
    /**
     * Look a value in the EventStore, and return its String value
     * @param key the key to use to look up a value
     * @return the String representation of the key, null if key doesn't exist
     */
	public String lookup(String key) {
		String val = null;
		if (key.equalsIgnoreCase("$NOW")) {
			Date now = new Date();
			return formatter.format(now);
		}
		if (key.charAt(0) == '$') {
			// EventStore es = EventStore.getThreadInstance();
			if (key.contains(":")) {
				// look up a message object
				String[] str = key.split(":");
				String first = str[0];
				if (first.startsWith("$msg[")) {
					MonitorMessage m = (MonitorMessage)get(str[0]);
					Object obj = m.get(str[1]);
					if (obj == null) {
						val = null;
					} else {
						val = m.get(str[1]).toString();
					}
				}
/* old code that looked up the current message.				
				else if (first.startsWith("$msg")){
					Object obj = msg.get(str[1]);
					val = (msg.get(str[1])).toString();
				}
*/
			} else {
				Object obj = get(key);
				if (obj == null) {
					val = null;
				} else
					val = obj.toString();
			}
		} else
			val = key;
		return val;
	}
}
