package lsst.ctrl.evmon.engine;

import java.util.Set;


public abstract class MonitorMessage {
	
	public abstract Object get(String key);
	
	public abstract void put(String key, Object value);
	
	public abstract Set<String> keySet();
}