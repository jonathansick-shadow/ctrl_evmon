package lsst.ctrl.evmon.output;

import javax.jms.TextMessage;

import lsst.ctrl.evmon.Template;
import lsst.ctrl.evmon.TemplateEntry;
import lsst.ctrl.evmon.engine.EventStore;


public class LsstEventWriter extends JmsWriter {
	static String TUPLECOUNT_PROPERTY = "LSSTEVENTTUPLES";
	
	public LsstEventWriter(String topic) {
		this(topic, defaultHost, defaultPort);
	}
	public LsstEventWriter(String topic, String host) {
		this(topic, host, defaultPort);
	}
	
	public LsstEventWriter(String topic, String host, int port) {
		super(topic, host, port);
	}
	
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