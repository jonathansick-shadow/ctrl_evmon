package lsst.ctrl.evmon.input;

import java.util.Vector;

import javax.jms.Message;

import lsst.ctrl.evmon.engine.MonitorMessage;
import lsst.ctrl.evmon.utils.DataProperty;
import lsst.ctrl.evmon.utils.DataPropertyNode;
import lsst.ctrl.evmon.utils.DataPropertyType;

import org.apache.activemq.command.ActiveMQTextMessage;


public class LsstMessage extends MonitorMessage {
	DataPropertyNode root = null;
	String text = null;

	static String TUPLECOUNT_PROPERTY = "LSSTEVENTTUPLES";
	static String TIMESTAMP_PROPERTY = "EventTimestamp";

	int nTuples = 0;
	String eventTimestamp = null;

	public LsstMessage(Message msg) {
//		super(msg);
		ActiveMQTextMessage textMsg = (ActiveMQTextMessage)msg;

		String text = null;
		try {
			nTuples = msg.getIntProperty(TUPLECOUNT_PROPERTY);
//			eventTimestamp = msg.getStringProperty(TIMESTAMP_PROPERTY);

			text = textMsg.getText();
			root = unmarshall(nTuples, text);
			System.out.println(text);
		} catch (Exception e) {
			System.err.println(e);
		}
	}
	
	public DataPropertyNode unmarshall(int nTuples, String text) {
		Vector<String> vec = null;
		Vector<String> vec2 = null;
		
		vec = splitString(text, "~~");
		
		String line = vec.elementAt(0);
		vec2 = splitString(line, "||");
		
		String type = vec2.elementAt(0);
		String key = vec2.elementAt(1);
		String val = vec2.elementAt(2);
		
		DataPropertyNode dpn = new DataPropertyNode(key);
		int pos = 1;
		
		DataPropertyType node = unmarshall(nTuples, dpn, vec, pos);
		
		return dpn;
	}
	
	public void put(String key, Object value) {
	}
	
	// TODO - This needs to have a counterpart that returns ALL instances of
	// objects that match "key"
	public Object get(String key) {
		for (int i = 0; i < root.size(); i++) {
			DataProperty dp = (DataProperty)root.get(i);
			if (dp.getKey().equals(key))
				return dp.getValue();
		}
		return null;
	}
	
	public DataPropertyType unmarshall(int nTuples, DataPropertyNode root, Vector<String> vec, int pos) {
	    String type;
	    String key;
	    String val;
	    
	    for (int i = 0; i < nTuples; i++) {
	        String root_name;
	        Vector<String> vec2;
	        String line = vec.elementAt(pos);
	        vec2 = splitString(line, "||");
	        
	        type = vec2.elementAt(0);
	        key = vec2.elementAt(1);
	        val = vec2.elementAt(2);
	        
	        if (type.equals("node")) {
	        	int int_value = Integer.parseInt(val);
	        	DataPropertyNode newroot = new DataPropertyNode(key);
	        	pos++;
	        	DataPropertyType node = unmarshall(int_value, newroot, vec, pos);
	        	root.add(newroot);
	        } else {
	        	Object value = null;
	            if (type.equals("int")) {
	                value = new Integer(val);
	            } else if (type.equals("bool")) {
/*
	                value = new Boolean(value);

	            } else if (type == "long long") {
	                long long longlong_value;
	                iss >> longlong_value;
	                value = boost::any(longlong_value);
*/
	            } else if (type.equals("long")) {
	                value = new Long(val);
	            } else if (type.equals("float")) {
	                value = new Float(val);
	            } else if (type.equals("double")) {
	                value = new Double(val);
	            } else if (type.equals("string")) {
	                value = new String(val); // XXX - this necessary?
	            }
	            DataProperty node = new DataProperty(key, value);
	            root.add(node);
	            pos++;
	        }
	    }
	    return root;
	}

	Vector<String> splitString(String s, String delim) {
		Vector<String> results = new Vector<String>();
		int cutAt;
		int delim_len = delim.length();
		int here = 0;
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