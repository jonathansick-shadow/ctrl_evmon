package lsst.ctrl.evmon.input;

import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;

import lsst.ctrl.evmon.engine.MonitorMessage;

import org.apache.activemq.command.ActiveMQMapMessage;


public class JmsMessage extends MonitorMessage {
	HashMap<String, Object> map = new HashMap<String, Object>();

	public JmsMessage(Message msg) {
		ActiveMQMapMessage message = (ActiveMQMapMessage)msg;
		
		Map<String, Object> content = null;
		try {
			content = message.getContentMap();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		map = new HashMap<String, Object>(content);
	}
	
	public Object get(String key) {
		return map.get(key);
	}
	
	public void put(String key, Object value) {
		map.put(key, value);
	}
}
