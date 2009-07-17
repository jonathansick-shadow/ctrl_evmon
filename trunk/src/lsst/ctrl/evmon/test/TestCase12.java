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
import lsst.ctrl.evmon.Span;
import lsst.ctrl.evmon.Template;
import lsst.ctrl.evmon.input.LsstEventReader;
import lsst.ctrl.evmon.output.ConsoleWriter;
import lsst.ctrl.evmon.output.LsstEventWriter;

public class TestCase12 {

	public TestCase12() {
	}

	public Job createJob() {
		
		Chain chain = new Chain();

		Condition cond1 = new Condition("$msg:info", Relation.STARTS_WITH,	"Starting Stage");
		chain.addLink(cond1);

		Span span = new Span("1",3);
		

		LogicalCompare comp1 = new LogicalCompare("$msg:info", Relation.STARTS_WITH, "Starting node");
		LogicalCompare comp2 = new LogicalCompare("$msg:CPU", Relation.EQUALS, Span.INDEX);
	
		LogicalAnd logicalAnd = new LogicalAnd(comp1, comp2);
		Condition reqs2 = new Condition(logicalAnd, span);
		chain.addLink(reqs2);
		
		SetTask task1 = new SetTask("$micros", "$msg:micros");
		chain.addLink(task1);



		LogicalCompare comp3 = new LogicalCompare("$msg:info", Relation.STARTS_WITH, "Ending node");
		LogicalCompare comp4 = new LogicalCompare("$msg:CPU", Relation.EQUALS, Span.INDEX);
		LogicalAnd logicalAnd2 = new LogicalAnd(comp3, comp4);
		
		Template exceptionTemplate = new Template();
		exceptionTemplate.put("INFO", Template.STRING, "Problem!");
		exceptionTemplate.put("CPU", Template.INT, Span.INDEX);

		LsstEventWriter exceptionWriter = new LsstEventWriter("warning");
		ExceptionTask exceptionTask = new ExceptionTask(exceptionWriter, exceptionTemplate);
		
		Condition reqs3 = new Condition(logicalAnd2, span);
		reqs3.setException(exceptionTask, 5000L);
		
		chain.addLink(reqs3);

		SetTask setTask = new SetTask("$delta","$msg[2]:micros - $msg[1]:micros");
		chain.addLink(setTask);
		
		Template template = new Template();
		template.put("INFO", Template.STRING, "Results for delta");
		template.put("DELTA", Template.INT, "$delta");
		
		LsstEventWriter writer2 = new LsstEventWriter("information");
		EventTask eventTask = new EventTask(writer2, template);
		chain.addLink(eventTask);
		
		ConsoleWriter outputWriter = new ConsoleWriter();
		EventTask eventTask2 = new EventTask(outputWriter, template);
		chain.addLink(eventTask2);
		
		Condition cond4 = new Condition("$msg:info", Relation.STARTS_WITH, "Ending Stage");
		chain.addLink(cond4);

  	    LsstEventReader reader = new LsstEventReader("monitor");
		Job job = new Job(reader, chain);

		return job;
	}

}