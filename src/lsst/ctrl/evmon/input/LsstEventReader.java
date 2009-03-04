package lsst.ctrl.evmon.input;

import javax.jms.Message;

import lsst.ctrl.evmon.engine.MonitorMessage;



public class LsstEventReader extends JmsReader {
	
	public LsstEventReader(String topic) {
		this(topic, defaultHost, defaultPort);
	}
	
	public LsstEventReader(String topic, String host) {
		this(topic, host, defaultPort);
	}
	
	public LsstEventReader(String topic, String host, int port) {
		super(topic, host, port);
	}
	
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