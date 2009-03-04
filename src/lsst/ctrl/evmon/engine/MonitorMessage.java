package lsst.ctrl.evmon.engine;


public abstract class MonitorMessage {
	
	public abstract Object get(String key);
	
	public abstract void put(String key, Object value);
	
}