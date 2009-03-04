package lsst.ctrl.evmon;

import lsst.ctrl.evmon.engine.EventStore;
import lsst.ctrl.evmon.engine.MonitorMessage;
import lsst.ctrl.evmon.output.MessageWriter;

public class EventTask implements Task, Link {
	Template template = null;
	MessageWriter writer = null;
	
	public EventTask(MessageWriter writer, Template template) {
		this.writer = writer;
		this.template = template;

	}
	
	public void execute(EventStore es, MonitorMessage event) {
		writer.send(es, template);
	}
}