package lsst.ctrl.evmon.input;

import javax.jms.Message;

import lsst.ctrl.evmon.engine.MonitorMessage;


/**
 * Class LsstEventReader reads events from LSST event topics
 */
public class LsstEventReader extends JmsReader {
	
    /**
     * Class constructor LsstEventReader reads messages from an event topic
     * on the default host (localhost) and default port.
     * @param topic the LSST event topic to subscribe to
     */
	public LsstEventReader(String topic) {
		this(topic, defaultHost, defaultPort);
	}
	
    /**
     * Class constructor LsstEventReader reads messages from an event topic
     * on the default host (localhost) and default port.
     * @param topic the LSST event topic to subscribe to
     * @param host the host of LSST event broker to connect to
     */
	public LsstEventReader(String topic, String host) {
		this(topic, host, defaultPort);
	}
	
    /**
     * Class constructor LsstEventReader reads messages from an event topic
     * on the default host (localhost) and default port.
     * @param topic the LSST event topic to subscribe to
     * @param host the host of LSST event broker to connect to
     * @param port the port which the event broker is listening to
     */
	public LsstEventReader(String topic, String host, int port) {
		super(topic, host, port);
	}
	
    /**     
     * Retrieve the next available message from the LsstEventReader's data source
     * @return MonitorMessage encapsulating the retrieved LSST message
     */ 
	public MonitorMessage getMessage() {
		MonitorMessage mon_message = null;
		Message message = null;
		try {
			message = consumer.receive();
			mon_message = new LsstMessage(message);
		} catch (Exception e) {
			System.err.println(e);
		}
		return mon_message;
	}
}
