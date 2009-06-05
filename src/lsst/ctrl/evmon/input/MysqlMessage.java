package lsst.ctrl.evmon.input;

import java.util.Set;

import lsst.ctrl.evmon.engine.MonitorMessage;
import lsst.ctrl.evmon.utils.MultiMap;


/**
 * Class MysqlMessages represents one returned line from a Mysql database query
 */
public class MysqlMessage extends MonitorMessage {
	MultiMap map = new MultiMap();

    /**
     * Method to retrieve a value     
     * @param key the key to use for lookup     
     * @return the object retrieved for this key.  If the key doesn't exist, ret
urn null
     */
	public Object get(String key) {
		return map.get(key);
	}

    /**
     * Method to put a key/value pair into this MysqlMessage
     * @param key the key to use for lookup
     * @param value the value to associate with this key
     */
	public void put(String key, Object value) {
		map.put(key, value);
	}
	
	
    /**
     * method to retrieve all the keys for this MysqlMessage
     * @return the Set of Strings representing all the keys for this MysqlMessage
     */
	public Set keySet() {
		return map.keySet();
	}

}
