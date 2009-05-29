package lsst.ctrl.evmon.input;

import java.net.URI;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import lsst.ctrl.evmon.engine.MonitorMessage;

import org.apache.activemq.ActiveMQConnectionFactory;


/**
 * Class JmsReader is used to read messages from a JMS topic.
 */
public class JmsReader implements MessageReader {

	private Destination destination;
	private Connection connection;
	private Session session;
	MessageConsumer consumer;
	
	static int defaultPort = 61616;
	static String defaultHost = "localhost";

    /**
     * Class construct JmsReader reads from a topic on "localhost" on the default
     * @param topic the JMS topic to subscribe to
     */
	public JmsReader(String topic) {
		this(topic, defaultHost, defaultPort);
	}
	
    /**
     * Class construct JmsReader reads from a topic on "localhost" on the default
     * @param topic the JMS topic to subscribe to
     * @param host the JMS broker host to connect to
     */
	public JmsReader(String topic, String host) {
		this(topic, host, defaultPort);
	}
	
    /**
     * Class construct JmsReader reads from a topic on "localhost" on the default
     * @param topic the JMS topic to subscribe to
     * @param host the JMS broker host to connect to
     * @param port the server port to connect to
     */
	public JmsReader(String topic, String host, int port) {
		openConnection(topic, host, port);
	}
	
	// TODO: add a way to add all the "extras" on to the end of the URI
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
		
		consumer = session.createConsumer(destination);

		} catch (Exception e) {
			System.err.println(e);
		}
	}
	
    /**
     * Retrieve the next available message from the Reader's data source
     * @return MonitorMessage encapsulating the retrieved message
     */
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
