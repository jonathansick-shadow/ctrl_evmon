package lsst.ctrl.evmon.engine;

import java.util.Set;


/**
 * Abstract class which is the internal representation of messages received 
 * from Readers and written to Writers.
 */
public abstract class MonitorMessage {
	
    /**
     * Abstract method to retrieve a value
     * @param key the key to use for lookup
     * @return the object retrieved for this key.  If the key doesn't exist, return null
     */
	public abstract Object get(String key);
	
    /**
     * Abstract method to put a key/value pair into this MonitorMessage
     * @param key the key to use for lookup
     * @param value the value to associate with this key
     */
	public abstract void put(String key, Object value);
	
    /**
     * Abstract method to retrieve all the keys for this MonitorMessage
     * @return the Set of Strings representing all the keys for this MonitorMessage
     */
	public abstract Set<String> keySet();
}
