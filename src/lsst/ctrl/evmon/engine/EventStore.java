package lsst.ctrl.evmon.engine;

import java.text.SimpleDateFormat;
import java.util.BitSet;
import java.util.Date;

import lsst.ctrl.evmon.utils.MultiMap;


public class EventStore {
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private MultiMap map = new MultiMap();
	
	public EventStore() {
		
	}
	
	public EventStore(MultiMap map) {
		this.map = map;
	}

	public MultiMap getMap() {
		return map;
	}
	
	public boolean exists(String key) {
		if (map.get(key) != null)
			return true;
		return false;
	}
	
	public Object get(String key) {
		return map.get(key);
	}
	
	public void put(String key, String value) {
		map.put(key, value);
	}

	public void put(String key, int value) {
		map.put(key, value);
	}
	
	public void put(String key, BitSet value) {
		map.put(key, value);
	}
	
	public void put(String key, MonitorMessage msg) {
		map.put(key, msg);
	}
	
	public EventStore clone() {
		return new EventStore(map.clone());
	}
	
	public String lookup(String token) {
		String val = null;
		if (token.equalsIgnoreCase("$NOW")) {
			Date now = new Date();
			return formatter.format(now);
		}
		if (token.charAt(0) == '$') {
			// EventStore es = EventStore.getThreadInstance();
			if (token.contains(":")) {
				// look up a message object
				String[] str = token.split(":");
				String first = str[0];
				if (first.startsWith("$msg[")) {
					MonitorMessage m = (MonitorMessage)get(str[0]);
					val = m.get(str[1]).toString();
				}
/* old code that looked up the current message.				
				else if (first.startsWith("$msg")){
					Object obj = msg.get(str[1]);
					val = (msg.get(str[1])).toString();
				}
*/
			} else {
				Object obj = get(token);
				val = obj.toString();
			}
		} else
			val = token;
		return val;
	}
}
