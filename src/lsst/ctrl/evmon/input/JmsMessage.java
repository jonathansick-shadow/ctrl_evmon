package lsst.ctrl.evmon.input;

import java.util.Map;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Message;

import lsst.ctrl.evmon.engine.MonitorMessage;
import lsst.ctrl.evmon.utils.MultiMap;

import org.apache.activemq.command.ActiveMQMapMessage;


/**
 * Class JmsMessage is a subclass of MonitorMessage.  This is meant to be used
 * to allow a message received via JMS to be treated as a MonitorMessage.
 */
public class JmsMessage extends MonitorMessage {
	MultiMap map = new MultiMap();

    /**
     * Class constructor JmsMessage takes a JMS message and encapsulates it
     * into a MonitorMessage, which can be used by the rest of the EventMonitor
     * infrastructure.
     */
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
     * Method to put a key/value pair into this MonitorMessage
     * @param key the key to use for lookup
     * @param value the value to associate with this key
     */
	public void put(String key, Object value) {
		map.put(key, value);
	}
	
    /**
     * Abstract method to retrieve all the keys for this MonitorMessage
     * @return the Set of Strings representing all the keys for this MonitorMess
age
     */
	public Set keySet() {
		return map.keySet();
	}
}
