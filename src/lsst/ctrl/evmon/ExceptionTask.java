package lsst.ctrl.evmon;

import lsst.ctrl.evmon.engine.EventStore;
import lsst.ctrl.evmon.output.MessageWriter;

public class ExceptionTask {
	MessageWriter writer = null;
	Template template = null;


	public ExceptionTask(MessageWriter writer, Template template) {
		this.writer = writer;
		this.template = template;
	}
	
	public ExceptionTask clone() {
		return new ExceptionTask(writer, template);
	}
	

	public void execute(EventStore es) {
			writer.send(es, template);
	}

}