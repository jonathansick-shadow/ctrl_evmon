package lsst.ctrl.evmon.test;

import lsst.ctrl.evmon.Chain;
import lsst.ctrl.evmon.Condition;
import lsst.ctrl.evmon.EventTask;
import lsst.ctrl.evmon.Job;
import lsst.ctrl.evmon.Relation;
import lsst.ctrl.evmon.SetTask;
import lsst.ctrl.evmon.Template;
import lsst.ctrl.evmon.input.LsstEventReader;
import lsst.ctrl.evmon.output.LsstEventWriter;

public class TestCase05 {

	public TestCase05() {
	}

	public Job createJob() {

		Chain chain = new Chain();

		Condition cond1 = new Condition("$msg:info", Relation.STARTS_WITH,
				"Starting pipeline");
		chain.addLink(cond1);

		Condition cond2 = new Condition("$msg:info", Relation.STARTS_WITH,
				"Ending pipeline");
		chain.addLink(cond2);

		SetTask setTask = new SetTask("$result",
				"$msg[1]:micros - $msg[0]:micros");

		chain.addLink(setTask);

		Template template = new Template();
		template.put("INFO", Template.STRING, "Results for delta");
		template.put("DELTA", Template.INT, "$result");

		LsstEventWriter writer = new LsstEventWriter("monitor");
		EventTask eventTask = new EventTask(writer, template);

		chain.addLink(eventTask);

  	    LsstEventReader reader = new LsstEventReader("monitor");
		Job job = new Job(reader, chain);

		return job;
	}
}