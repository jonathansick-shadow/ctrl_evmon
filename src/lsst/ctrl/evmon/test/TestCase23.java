package lsst.ctrl.evmon.test;

import lsst.ctrl.evmon.Chain;
import lsst.ctrl.evmon.Condition;
import lsst.ctrl.evmon.ConvertDateTask;
import lsst.ctrl.evmon.EventTask;
import lsst.ctrl.evmon.Job;
import lsst.ctrl.evmon.LogicalAnd;
import lsst.ctrl.evmon.LogicalCompare;
import lsst.ctrl.evmon.MysqlTask;
import lsst.ctrl.evmon.NormalizeMessageFilter;
import lsst.ctrl.evmon.Relation;
import lsst.ctrl.evmon.SetTask;
import lsst.ctrl.evmon.Template;
import lsst.ctrl.evmon.input.LsstEventReader;
import lsst.ctrl.evmon.input.MysqlReader;
import lsst.ctrl.evmon.output.ConsoleWriter;
import lsst.ctrl.evmon.output.MysqlWriter;

public class TestCase23 {

	public Job createJob() {
		
		String runId = "rlp1127";
		String query = "SELECT date, nanos, id, sliceid, runid, level, log, custom, hostid, status, pipeline from logger where runid='"
				+ runId + "' and log='harness.slice.visit.stage.process' order by nanos;";

		String logName = "harness.slice.visit.stage.process";
		Chain chain = new Chain();

		LogicalCompare cond3 = new LogicalCompare("$msg:status", Relation.EQUALS, "start");
		Condition firstCondition = new Condition(cond3);
		chain.addLink(firstCondition);

		SetTask setTask1 = new SetTask("$firstLoop", "$msg:loopnum");
		chain.addLink(setTask1);

		SetTask setTask3 = new SetTask("$startdate", "$msg:date");
		chain.addLink(setTask3);

		LogicalCompare comp1 = new LogicalCompare("$msg:status", Relation.EQUALS, "end");		
		LogicalCompare comp2 = new LogicalCompare("$msg:sliceid", Relation.EQUALS, "$msg[0]:sliceid");
		LogicalCompare comp3 = new LogicalCompare("$msg:runid", Relation.EQUALS, "$msg[0]:runid");
		LogicalCompare comp4 = new LogicalCompare("$msg:loopnum", Relation.EQUALS, "$msg[0]:loopnum");
		LogicalCompare comp5 = new LogicalCompare("$msg:hostid", Relation.EQUALS, "$msg[0]:hostid");
		LogicalCompare comp6 = new LogicalCompare("$msg:stageId", Relation.EQUALS, "$msg[0]:stageId");

		LogicalAnd logicalAnd1 = new LogicalAnd(comp1, comp2);
		logicalAnd1.add(comp3);
		logicalAnd1.add(comp4);
		logicalAnd1.add(comp5);
		logicalAnd1.add(comp6);

		Condition cond2 = new Condition(logicalAnd1);
		chain.addLink(cond2);
		
		SetTask setTask4 = new SetTask("$duration", "$msg[1]:nanos-$msg[0]:nanos");
		chain.addLink(setTask4);

		SetTask setTask5 = new SetTask("$id", "$msg:id");
		chain.addLink(setTask5);

		Template template = new Template();
		template.put("INFO", Template.STRING, "Results for time delta");
		template.put("sliceId", Template.STRING, "$msg[0]:sliceid");
		template.put("runId", Template.STRING, "$msg[0]:runid");
		template.put("name", Template.STRING, "$msg[0]:log");
		template.put("duration", Template.INT, "$duration");
		template.put("host", Template.STRING, "$msg[0]:hostid");
		template.put("loopnum", Template.INT, "$msg[0]:loopnum");
		template.put("stageId", Template.INT, "$msg[0]:stageId");


		// write to console
		ConsoleWriter outputWriter = new ConsoleWriter();
		EventTask eventTask = new EventTask(outputWriter, template);
		chain.addLink(eventTask);

		// write to database
		String insertQuery = "INSERT INTO test_events.durations_process(runid, name, sliceid, duration, host, loopnum, pipeline, date, stageid) values({$msg:runid}, {$msg:log}, {$msg:sliceid}, {$duration}, {$msg:hostid}, {$firstLoop}, {$msg:pipeline}, {$startdate}, {$msg:stageId});";
		MysqlWriter mysqlWriter = new MysqlWriter("ds33", "test_events", "srp", "LSSTdata");		
		MysqlTask mysqlTask = new MysqlTask(mysqlWriter, insertQuery);
		chain.addLink(mysqlTask);
	
		MysqlReader mysqlReader = new MysqlReader("ds33.ncsa.uiuc.edu",
				"test_events", "srp", "LSSTdata");
		mysqlReader.setFilter(new NormalizeMessageFilter("custom", "=", ";"));
		
		mysqlReader.setSelectString(query);

		Job job = new Job(mysqlReader, chain);

		return job;
	}
}
