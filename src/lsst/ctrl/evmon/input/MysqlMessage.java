package lsst.ctrl.evmon.input;

import java.util.Set;

import lsst.ctrl.evmon.engine.MonitorMessage;
import lsst.ctrl.evmon.utils.MultiMap;


public class MysqlMessage extends MonitorMessage {
	MultiMap map = new MultiMap();

	public Object get(String key) {
		return map.get(key);
	}

	public void put(String key, Object value) {
		map.put(key, value);
	}
	
	
	public Set keySet() {
		return map.keySet();
	}

}