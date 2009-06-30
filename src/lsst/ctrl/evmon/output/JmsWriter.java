package lsst.ctrl.evmon.output;

import java.net.URI;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;

import lsst.ctrl.evmon.Template;
import lsst.ctrl.evmon.engine.EventStore;

import org.apache.activemq.ActiveMQConnectionFactory;


/**
 * Class JmsWriter writes messages to a JMS topic
 */
public class JmsWriter implements MessageWriter {

	private Destination destination;
	private Connection connection;
	public  Session session;
	public MessageProducer producer;
	
	static String defaultHost = "localhost";
	static int defaultPort = 61616;
	
    /**
     * Class constructor which connects to "localhost" on the default port (61616)
     * and subscribes to a topic
     * @param topic the topic to subscribe to
     */
	public JmsWriter(String topic) {
		this(topic, defaultHost, defaultPort);
	}

    /**
     * Class constructor which connects to a host on the default port (61616)
     * and subscribes to a topic
     * @param topic the topic to subscribe to
     * @param host the host to connect to
     */
	public JmsWriter(String topic, String host) {
		this(topic, host, defaultPort);
	}
	
    /**
     * Class constructor which connects to a host on a socket port
     * and subscribes to a topic
     * @param topic the topic to subscribe to
     * @param host the host to connect to
     * @param port the port number to connect to
     */
	public JmsWriter(String topic, String host, int port) {
		try {
			openConnection(topic, host, port);
		} catch (Exception e) {
			System.err.println(e);
		}
	}	
	

	private void openConnection(String topic, String host, int port) {
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
			System.out.println("pffft!");
			System.err.println(e);
		}
	}
	
    /** 
     * Currently unimplemented.
     */
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
