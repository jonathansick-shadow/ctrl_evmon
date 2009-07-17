package lsst.ctrl.evmon.output;

import javax.jms.TextMessage;

import lsst.ctrl.evmon.Template;
import lsst.ctrl.evmon.TemplateEntry;
import lsst.ctrl.evmon.engine.EventStore;


public class LsstEventWriter extends JmsWriter {
	static String TUPLECOUNT_PROPERTY = "LSSTEVENTTUPLES";
	
    /**
     * Class constructor to create a connection to the default host and port for a topic
     * to write LSST Events
     *
     * @param topic the topic to subscribe to
     */
	public LsstEventWriter(String topic) {
		this(topic, defaultHost, defaultPort);
	}

    /**
     * Class constructor to create a connection to a host (and default port) for a topic
     * to write LSST Events.
     *
     * @param topic the topic to subscribe to
     * @param host the event broker host to connect to
     */
	public LsstEventWriter(String topic, String host) {
		this(topic, host, defaultPort);
	}
	
    /**
     * Class constructor to create a connection to the default host and port for a topic
     * to write LSST Events
     *
     * @param topic the topic to subscribe to
     * @param host the event broker host to connect to
     * @param port the event broker port
     */
	public LsstEventWriter(String topic, String host, int port) {
		super(topic, host, port);
	}
	
    /**
     * Send an LSST Event.
     *
     * @param es The EventStore to use to look up variables in the templtes
     * @param template the keys/values to send in the LSST Event
     */
	public void send(EventStore es, Template template) {
		// EventStore es = EventStore.getThreadInstance();
		String retVal = "node||root||" + template.size() + "~~";
		for (int i = 0; i < template.size(); i++) {

			TemplateEntry entry = (TemplateEntry) template.get(i);

			retVal += entry.getTypeString() + "||" + entry.getKey() + "||";
			String value = (String) entry.getValue();
			if (value.charAt(0) == '$') {
				String val = null;

				val = (String)es.lookup(value);
				retVal += val;

				System.out.println("sendMessage: " + entry.getKey() + " = "
						+ val);
			} else {
				retVal += value;
				System.out.println("sendMessage: " + entry.getKey() + " = "
						+ value);
			}
			retVal += "~~";
		}
		System.out.println("sending \"" + retVal + "\"");

		try {
			TextMessage msg = session.createTextMessage();
			msg.setIntProperty(TUPLECOUNT_PROPERTY, template.size());
			msg.setText(retVal);
			producer.send(msg);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
