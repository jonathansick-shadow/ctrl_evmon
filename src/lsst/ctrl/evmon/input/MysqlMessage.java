package lsst.ctrl.evmon.input;

import java.util.HashMap;

import lsst.ctrl.evmon.engine.MonitorMessage;


public class MysqlMessage extends MonitorMessage {
	HashMap<String, Object> map = new HashMap<String, Object>();

	public Object get(String key) {
		return map.get(key);
	}

	public void put(String key, Object value) {
		map.put(key, value);
	}

}