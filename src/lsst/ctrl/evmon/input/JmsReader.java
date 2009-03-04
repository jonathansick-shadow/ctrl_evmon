package lsst.ctrl.evmon.input;

import java.net.URI;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import lsst.ctrl.evmon.engine.MonitorMessage;

import org.apache.activemq.ActiveMQConnectionFactory;



public class JmsReader implements MessageReader {

	private Destination destination;
	private Connection connection;
	private Session session;
	MessageConsumer consumer;
	
	static int defaultPort = 61616;
	static String defaultHost = "localhost";

	public JmsReader(String topic) {
		this(topic, defaultHost, defaultPort);
	}
	
	public JmsReader(String topic, String host) {
		this(topic, host, defaultPort);
	}
	
	public JmsReader(String topic, String host, int port) {
		openConnection(topic, host, port);
	}
	
	// TODO: add a way to add all the "extras" on to the end of the URI
	public void openConnection(String topic, String host, int port) {
		
		String brokerURI = "tcp://"+host+":"+port+
		    "?jms.useAsyncSend=true";
		
		ActiveMQConnectionFactory connectionFactory =
			new ActiveMQConnectionFactory(brokerURI);
		
		try {
		connection = connectionFactory.createConnection();
		
		connection.start();
		
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		
		destination = session.createTopic(topic);
		
		consumer = session.createConsumer(destination);

		} catch (Exception e) {
			System.err.println(e);
		}
	}
	
	public MonitorMessage getMessage() {
		MonitorMessage mon_message = null;
		Message message = null;
		try {
			message = consumer.receive();
			mon_message = new JmsMessage(message);
		} catch (Exception e) {
			System.err.println(e);
		}
		return mon_message;
	}
}