package lsst.ctrl.evmon.test;

import lsst.ctrl.evmon.Chain;
import lsst.ctrl.evmon.Condition;
import lsst.ctrl.evmon.EventTask;
import lsst.ctrl.evmon.Job;
import lsst.ctrl.evmon.LogicalAnd;
import lsst.ctrl.evmon.LogicalCompare;
import lsst.ctrl.evmon.Relation;
import lsst.ctrl.evmon.SetTask;
import lsst.ctrl.evmon.Span;
import lsst.ctrl.evmon.Template;
import lsst.ctrl.evmon.input.LsstEventReader;
import lsst.ctrl.evmon.output.LsstEventWriter;

public class TestCase13 {

	public TestCase13() {
	}

	public Job createJob() {

		Chain chain = new Chain();

		Condition cond1 = new Condition("$msg:info", Relation.STARTS_WITH,	"Starting Stage");
		chain.addLink(cond1);

		Span span = new Span(1,3);


		LogicalCompare comp1 = new LogicalCompare("$msg:info", Relation.STARTS_WITH, "Starting node");
		LogicalCompare comp2 = new LogicalCompare("$msg:CPU", Relation.EQUALS, Span.INDEX);
		LogicalCompare comp3 = new LogicalCompare("$msg:value", Relation.GREATER_THAN, 0);
		
		LogicalAnd logicalAnd1 = new LogicalAnd(comp1, comp2);
		logicalAnd1.add(comp3);

		Condition cond2 = new Condition(logicalAnd1, span);
		chain.addLink(cond2);
		
		SetTask task1 = new SetTask("$micros", "$msg:micros");
		chain.addLink(task1);



		LogicalCompare comp4 = new LogicalCompare("$msg:info", Relation.STARTS_WITH, "Ending node");
		LogicalCompare comp5 = new LogicalCompare("$msg:CPU", Relation.EQUALS, Span.INDEX);
		LogicalAnd logicalAnd2 = new LogicalAnd(comp4, comp5);
		
		Condition cond3 = new Condition(logicalAnd2, span);
		chain.addLink(cond3);

		SetTask setTask = new SetTask("$delta","$msg[2]:micros - $msg[1]:micros");
		chain.addLink(setTask);
		
		Template template = new Template();
		template.put("INFO", Template.STRING, "Results for delta");
		template.put("DELTA", Template.INT, "$delta");

		LsstEventWriter lsstWriter = new LsstEventWriter("warning");
		EventTask eventTask = new EventTask(lsstWriter, template);
		chain.addLink(eventTask);
	
		Condition cond4 = new Condition("$msg:info", Relation.STARTS_WITH, "Ending Stage");
		chain.addLink(cond4);

  	    LsstEventReader reader = new LsstEventReader("monitor");
		Job job = new Job(reader, chain);

		return job;
	}

}