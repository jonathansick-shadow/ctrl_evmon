package lsst.ctrl.evmon.test;

import lsst.ctrl.evmon.Assertion;
import lsst.ctrl.evmon.Chain;
import lsst.ctrl.evmon.Condition;
import lsst.ctrl.evmon.EventTask;
import lsst.ctrl.evmon.ExceptionTask;
import lsst.ctrl.evmon.Job;
import lsst.ctrl.evmon.LogicalCompare;
import lsst.ctrl.evmon.Relation;
import lsst.ctrl.evmon.SetTask;
import lsst.ctrl.evmon.Template;
import lsst.ctrl.evmon.input.LsstEventReader;
import lsst.ctrl.evmon.output.LsstEventWriter;

public class TestCase06 {

	public TestCase06() {
	}

	public Job createJob() {
		
		Chain chain = new Chain();

		Condition cond1 = new Condition("$msg:info", Relation.STARTS_WITH,
				"Starting pipeline");
		chain.addLink(cond1);

		Template exceptionTemplate = new Template();
		exceptionTemplate.put("INFO", Template.STRING, "Problem!");

		LsstEventWriter writer = new LsstEventWriter("warning");
		ExceptionTask exceptionTask = new ExceptionTask(writer,
				exceptionTemplate);

		Condition cond2 = new Condition("$msg:info", Relation.STARTS_WITH,
				"Ending pipeline");
		cond2.setException(exceptionTask, 5000L);
		chain.addLink(cond2);

		Assertion assertion = new Assertion(new LogicalCompare("$msg[1]:micros",
				Relation.LESS_THAN, 0), exceptionTask);
		chain.addLink(assertion);

		SetTask setTask = new SetTask("$result",
				"$msg[1]:micros - $msg[0]:micros");

		chain.addLink(setTask);

		Template template = new Template();
		template.put("INFO", Template.STRING, "Results for delta");
		template.put("DELTA", Template.INT, "$result");

		LsstEventWriter writer2 = new LsstEventWriter("data");
		EventTask eventTask = new EventTask(writer2, template);

		chain.addLink(eventTask);

  	    LsstEventReader reader = new LsstEventReader("monitor");
		Job job = new Job(reader, chain);

		return job;
	}
}