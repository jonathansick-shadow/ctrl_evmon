package lsst.ctrl.evmon.output;

import java.net.URI;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;

import lsst.ctrl.evmon.Template;
import lsst.ctrl.evmon.engine.EventStore;

import org.apache.activemq.ActiveMQConnectionFactory;


public class JmsWriter implements MessageWriter {

	private Destination destination;
	private Connection connection;
	public  Session session;
	public MessageProducer producer;
	
	static String defaultHost = "localhost";
	static int defaultPort = 61616;
	
	public JmsWriter(String topic) {
		this(topic, defaultHost, defaultPort);
	}
	public JmsWriter(String topic, String host) {
		this(topic, host, defaultPort);
	}
	
	public JmsWriter(String topic, String host, int port) {
		try {
			openConnection(topic, host, port);
		} catch (Exception e) {
			System.err.println(e);
		}
	}	
	
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
		
		producer = session.createProducer(destination);

		} catch (Exception e) {
			System.err.println(e);
		}
	}
	
	public void send(EventStore es, Template template) {
/*
		Message message = null;
			try {
				message = consumer.receive();
				mon_message = new MonitorMessage(message);
			} catch (Exception e) {
				System.err.println(e);
			}
			return mon_message;
		}
*/	
	}
}
