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

public class TestCase04 {

	public TestCase04() {
	}

	public Job createJob() {
		String host = "fester.ncsa.uiuc.edu";

		String monitorTopic = "monitor";
		String resultsTopic = "results";


		Chain chain = new Chain();


		chain.addLink(new Condition("$msg:info", Relation.STARTS_WITH, "Starting pipeline"));

		chain.addLink(new Condition("$msg:info", Relation.STARTS_WITH, "Ending pipeline"));

		chain.addLink(new SetTask("$result", "$msg[1]:micros-$msg[0]:micros"));

		Template template = new Template();
		template.put("info", Template.STRING, "Results for delta");
		template.put("delta", Template.INT, "$result");

		LsstEventWriter writer = new LsstEventWriter(resultsTopic, host);
		chain.addLink(new EventTask(writer, template));

		LsstEventReader reader = new LsstEventReader(monitorTopic, host);
		Job job = new Job(reader, chain);
		return job;
	}
}