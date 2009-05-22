package lsst.ctrl.evmon.input;

import java.math.BigInteger;
import java.util.Set;
import java.util.Vector;

import javax.jms.Message;

import lsst.ctrl.evmon.engine.MonitorMessage;
import lsst.ctrl.evmon.utils.MultiMap;

import org.apache.activemq.command.ActiveMQTextMessage;

public class LsstMessage extends MonitorMessage {
	MultiMap map = new MultiMap();
	String text = null;

	String eventTimestamp = null;

	public LsstMessage(Message msg) {
		// super(msg);
		ActiveMQTextMessage textMsg = (ActiveMQTextMessage) msg;

		String text = null;
		try {
			text = textMsg.getText();
			System.out.println(text);
			unmarshall(text);
		} catch (Exception e) {
			System.err.println(e);
		}
	}
	
	public Set keySet() {
		return map.keySet();
	}

	public void unmarshall(String text) {
		Vector<String> vec = null;
		Vector<String> vec2 = null;

		vec = splitString(text, "~~");

		String line = vec.elementAt(0);
		vec2 = splitString(line, "||");
/*
		String type = vec2.elementAt(0);
		String key = vec2.elementAt(1);
		String val = vec2.elementAt(2);

		int nodeCount = Integer.valueOf(val);
*/
		for (int i = 1; i < vec.size(); i++) {
				Vector<String> v = splitString(vec.elementAt(i), "||");
				unmarshall(v);
		}

	}

	public void put(String key, Object value) {
	}

	// TODO - This needs to have a counterpart that returns ALL instances of
	// objects that match "key"
	public Object get(String key) {
		return map.get(key);
	}

	public void unmarshall(Vector<String> vec) {
		String type;
		String key;
		String val;

		type = vec.elementAt(0);
		key = vec.elementAt(1);
		val = vec.elementAt(2);

		Object value = null;
		if (type.equals("int")) {
			value = new Integer(val);
		} else if (type.equals("long")) {
			value = new Long(val);
		} else if (type.equals("float")) {
			value = new Float(val);
		} else if (type.equals("double")) {
			value = new Double(val);
		} else if (type.equals("string")) {
			value = new String(val); // XXX - this necessary?
		} else if (type.equals("datetime")) {
			value = new BigInteger(val);
		}
		
		map.put(key, value);
	}

	Vector<String> splitString(String s, String delim) {
		Vector<String> results = new Vector<String>();
		int cutAt;
		int delim_len = delim.length();

		String str = s;
		while ((cutAt = str.indexOf(delim)) != -1) {
			if (cutAt > 0) {
				results.add(str.substring(0, cutAt));
			}
			str = str.substring(cutAt + delim_len);
		}
		if (str.length() > 0) {
			results.add(str);
		}
		return results;
	}
}