package lsst.ctrl.evmon.test;

import lsst.ctrl.evmon.Chain;
import lsst.ctrl.evmon.Condition;
import lsst.ctrl.evmon.EventTask;
import lsst.ctrl.evmon.ExceptionTask;
import lsst.ctrl.evmon.Job;
import lsst.ctrl.evmon.LogicalAnd;
import lsst.ctrl.evmon.LogicalCompare;
import lsst.ctrl.evmon.Relation;
import lsst.ctrl.evmon.SetTask;
import lsst.ctrl.evmon.Template;
import lsst.ctrl.evmon.input.LsstEventReader;
import lsst.ctrl.evmon.output.LsstEventWriter;

public class TestCase08 {

	public TestCase08() {
	}

	public Job createJob() {

		Chain chain = new Chain();

		Condition cond1 = new Condition("$msg:info", Relation.STARTS_WITH,
				"Starting pipeline");
		chain.addLink(cond1);

		Template exceptionTemplate = new Template();
		exceptionTemplate.put("INFO", Template.STRING, "Problem!");

		LsstEventWriter exceptionWriter = new LsstEventWriter("monitor");
		ExceptionTask exceptionTask = new ExceptionTask(exceptionWriter,
				exceptionTemplate);


		LogicalCompare comp1 = new LogicalCompare("$msg:info", Relation.STARTS_WITH,
				"Ending pipeline");
		LogicalCompare comp2 = new LogicalCompare("$msg:HOST", Relation.EQUALS, "$msg[0]:HOST");

		LogicalAnd logicalAnd = new LogicalAnd(comp1, comp2);
		Condition reqs2 = new Condition(logicalAnd);
		reqs2.setException(exceptionTask, 5000L);
		chain.addLink(reqs2);

		SetTask setTask = new SetTask("$result",
				"$msg[1]:micros - $msg[0]:micros");

		chain.addLink(setTask);

		Template template = new Template();
		template.put("info", Template.STRING, "Pipeline time in microseconds");
		template.put("micros", Template.INT, "$result");

		LsstEventWriter writer = new LsstEventWriter("information");
		EventTask eventTask = new EventTask(writer, template);

		chain.addLink(eventTask);

  	    LsstEventReader reader = new LsstEventReader("monitor");
		Job job = new Job(reader, chain);
		return job;
	}

}
