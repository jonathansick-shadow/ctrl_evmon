package lsst.ctrl.evmon.test;

import lsst.ctrl.evmon.Chain;
import lsst.ctrl.evmon.Condition;
import lsst.ctrl.evmon.EventTask;
import lsst.ctrl.evmon.Job;
import lsst.ctrl.evmon.LogicalAnd;
import lsst.ctrl.evmon.LogicalCompare;
import lsst.ctrl.evmon.Relation;
import lsst.ctrl.evmon.Template;
import lsst.ctrl.evmon.input.LsstEventReader;
import lsst.ctrl.evmon.output.LsstEventWriter;

public class TestCase07 {

	public TestCase07() {
	}

	public Job createJob() {

		Chain chain = new Chain();

		Condition cond1 = new Condition("$msg:info", Relation.STARTS_WITH,
				"Starting pipeline");
		chain.addLink(cond1);


		LogicalCompare comp1 = new LogicalCompare("$msg:info", Relation.STARTS_WITH,
				"Ending pipeline");
		LogicalCompare comp2 = new LogicalCompare("$msg:HOST", Relation.EQUALS, "$msg[0]:HOST");

		LogicalAnd logicalAnd = new LogicalAnd(comp1, comp2);
		Condition reqs2 = new Condition(logicalAnd);
		chain.addLink(reqs2);

		Template template = new Template();
		template.put("info", Template.STRING, "packet number");
		template.put("packet", Template.STRING, "DONE");
		template.put("host", Template.STRING, "$msg[0]:HOST");

		LsstEventWriter writer = new LsstEventWriter("monitor");
		EventTask eventTask = new EventTask(writer, template);

		chain.addLink(eventTask);

  	    LsstEventReader reader = new LsstEventReader("monitor");
		Job job = new Job(reader, chain);;

		return job;
	}

}
