package lsst.ctrl.evmon;

import lsst.ctrl.evmon.engine.EventStore;
import lsst.ctrl.evmon.engine.MonitorMessage;
import lsst.ctrl.evmon.output.MessageWriter;

/**
 * Class EventTask takes a Template object and writes a message via a MessageWriter.  This is executed
 * as soon as it is reached in the Chain.  The format of the message is completely up to the MessageWriter object.
 */
public class EventTask implements Task, Link {
	Template template = null;
	MessageWriter writer = null;
	
    /**
     * Class constructor that specifies a MessageWriter and Template
     * @param writer the MessageWriter to use to write the message
     * @param template the Template to evaluate and send
     */
	public EventTask(MessageWriter writer, Template template) {
		this.writer = writer;
		this.template = template;

	}
	
    /**
     * Sends the Template to the MessageWriter, which formats it appropriately and writes it.  The way the
     * message is formatted and sent is up to the MessageWriter.
     *
     * @param es The EventStore to send to the MessageWriter
     * @param event (currently unused, and used as a placeholder). not evaluated.
     */
	public void execute(EventStore es, MonitorMessage event) {
		writer.send(es, template);
	}
}
