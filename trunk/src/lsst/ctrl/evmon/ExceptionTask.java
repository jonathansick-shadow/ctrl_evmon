package lsst.ctrl.evmon;

import lsst.ctrl.evmon.engine.EventStore;
import lsst.ctrl.evmon.output.MessageWriter;

/**
 * The class ExceptionTask is used to indicate what do do when an event monitor exception occurs.  Currently the only types of
 * exceptions that occur are timeouts within Conditions, which indicate that no incoming messages that met that Condition were
 * received within the timeout period.
 *
 * When an exception occurs within the event monitor, the Template is sent to a MessageWriter, and all further activity for this
 * ceases.
 */
public class ExceptionTask {
	MessageWriter writer = null;
	Template template = null;


    /**
     * Constructor class which specifies which MessageWriter and Template to use.
     * @param writer MessageWriter to use to write messages
     * @param template Template to send to the message writer
     */
	public ExceptionTask(MessageWriter writer, Template template) {
		this.writer = writer;
		this.template = template;
	}
	
    /**
     * Creates a duplicate of this object
     *
     * @return a copy of this object
     */
	public ExceptionTask clone() {
		return new ExceptionTask(writer, template);
	}
	

    /**
     * Sends the Template to the MessageWriter evaluation and output.
     */
	public void execute(EventStore es) {
			writer.send(es, template);
	}

}
