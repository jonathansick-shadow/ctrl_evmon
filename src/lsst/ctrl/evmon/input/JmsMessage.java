package lsst.ctrl.evmon.input;

import java.util.Map;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Message;

import lsst.ctrl.evmon.engine.MonitorMessage;
import lsst.ctrl.evmon.utils.MultiMap;

import org.apache.activemq.command.ActiveMQMapMessage;


public class JmsMessage extends MonitorMessage {
	MultiMap map = new MultiMap();

	public JmsMessage(Message msg) {
		ActiveMQMapMessage message = (ActiveMQMapMessage)msg;
		
		Map<String, Object> content = null;
		try {
			content = message.getContentMap();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		map = new MultiMap();
		map.setMap(content);
	}
	
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
