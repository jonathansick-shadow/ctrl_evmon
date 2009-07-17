package lsst.ctrl.evmon.test;

import lsst.ctrl.evmon.Chain;
import lsst.ctrl.evmon.Condition;
import lsst.ctrl.evmon.EventTask;
import lsst.ctrl.evmon.Job;
import lsst.ctrl.evmon.LogicalAnd;
import lsst.ctrl.evmon.LogicalCompare;
import lsst.ctrl.evmon.MysqlTask;
import lsst.ctrl.evmon.Relation;
import lsst.ctrl.evmon.SetTask;
import lsst.ctrl.evmon.Template;
import lsst.ctrl.evmon.input.LsstEventReader;
import lsst.ctrl.evmon.output.ConsoleWriter;
import lsst.ctrl.evmon.output.MysqlWriter;

public class TestCase22 {

	public Job createJob() {
		String runId = "SRP3521";

		Chain chain = new Chain();

		LogicalCompare cond1 = new LogicalCompare("$msg:LOG", Relation.EQUALS,
				"harness.pipeline.visit");
		LogicalCompare cond3 = new LogicalCompare("$msg:sliceId",
				Relation.EQUALS, "-1");
		LogicalCompare cond4 = new LogicalCompare("$msg:runId",
				Relation.EQUALS, runId);
		LogicalAnd firstAnd = new LogicalAnd(cond1, cond3);
		firstAnd.add(cond4);
		Condition firstCondition = new Condition(firstAnd);
		chain.addLink(firstCondition);

		SetTask setTask1 = new SetTask("$firstLoop", "$msg:loopnum");
		chain.addLink(setTask1);

		SetTask setTask2 = new SetTask("$nextLoop", "$msg:loopnum+1");
		chain.addLink(setTask2);

		LogicalCompare comp1 = new LogicalCompare("$msg:LOG", Relation.EQUALS,
				"harness.pipeline.visit");
		LogicalCompare comp2 = new LogicalCompare("$msg:loopnum",
				Relation.EQUALS, "$nextLoop");
		LogicalCompare comp3 = new LogicalCompare("$msg:hostId",
				Relation.EQUALS, "$msg[0]:hostId");

		LogicalAnd logicalAnd1 = new LogicalAnd(comp1, comp2);
		logicalAnd1.add(comp3);

		Condition cond2 = new Condition(logicalAnd1);
		chain.addLink(cond2);

		SetTask setTask3 = new SetTask("$duration", "$msg[1]:TIMESTAMP-$msg[0]:TIMESTAMP");
		chain.addLink(setTask3);

		SetTask setTask4 = new SetTask("$id", "$msg:id");
		chain.addLink(setTask4);

		Template template = new Template();
		template.put("INFO", Template.STRING, "Results for time delta");
		template.put("runId", Template.STRING, runId);
		template.put("name", Template.STRING, "$msg[0]:LOG");
		template.put("sliceId", Template.STRING, "$msg[0]:sliceId");
		template.put("duration", Template.INT, "$duration");
		template.put("host", Template.STRING, "$msg[0]:hostId");
		template.put("loopnum", Template.INT, "$msg[0]:loopnum");


		// write to console
		ConsoleWriter outputWriter = new ConsoleWriter();
		EventTask eventTask = new EventTask(outputWriter, template);
		chain.addLink(eventTask);

		// write to database
		String query = "INSERT INTO test_events.durations(runid, name, sliceid, duration, host, loopnum, pipeline) values({$msg:runId}, {$msg:LOG}, {$msg:sliceId}, {$duration}, {$msg:hostId}, {$firstLoop}, {$msg:pipeline});";
		MysqlWriter mysqlWriter = new MysqlWriter("ds33", "test_events", "srp", "LSSTdata");		
		MysqlTask mysqlTask = new MysqlTask(mysqlWriter, query);
		chain.addLink(mysqlTask);
		
		LsstEventReader reader = new LsstEventReader("LSSTLogging",
				"lsst4.ncsa.uiuc.edu");

		Job job = new Job(reader, chain);

		return job;
	}
}
