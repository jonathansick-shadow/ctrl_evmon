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
import lsst.ctrl.evmon.input.MysqlReader;
import lsst.ctrl.evmon.output.ConsoleWriter;
import lsst.ctrl.evmon.output.MysqlWriter;

public class TestCase20 {

	public Job createJob() {
		String runId = "rlp1127";
		String query = "SELECT date, nanos, id, sliceid, runid, level, log, comment, custom, hostid, status, pipeline from logger where runid='"
				+ runId + "' and log='harness.pipeline.visit' order by nanos;";

		Chain chain = new Chain();

		LogicalCompare cond1 = new LogicalCompare("$msg:log", Relation.EQUALS, "harness.pipeline.visit");
		LogicalCompare cond2 = new LogicalCompare("$msg:status", Relation.EQUALS, "start");
		LogicalCompare cond3 = new LogicalCompare("$msg:sliceid", Relation.EQUALS, "-1");
		LogicalCompare cond4 = new LogicalCompare("$msg:runid", Relation.EQUALS, runId);
		
		LogicalAnd firstAnd = new LogicalAnd(cond1, cond2);
		firstAnd.add(cond3);
		firstAnd.add(cond4);
		Condition firstCondition = new Condition(firstAnd);
		chain.addLink(firstCondition);

		SetTask setTask1 = new SetTask("$firstLoop", "$msg:loopnum");
		chain.addLink(setTask1);
		
		SetTask setTask2 = new SetTask("$nextLoop", "$msg:loopnum+1");
		chain.addLink(setTask2);
		SetTask setTask2a = new SetTask("$host", "$msg:hostid");
		chain.addLink(setTask2a);
		
		LogicalCompare comp1 = new LogicalCompare("$msg:log", Relation.EQUALS, "harness.pipeline.visit");
		LogicalCompare comp2 = new LogicalCompare("$msg:loopnum", Relation.EQUALS, "$nextLoop");
		LogicalCompare comp3 = new LogicalCompare("$msg:hostid", Relation.EQUALS, "$host");
		LogicalCompare comp4 = new LogicalCompare("$msg:status", Relation.EQUALS, "start");
		
		LogicalAnd logicalAnd1 = new LogicalAnd(comp1, comp2);
		logicalAnd1.add(comp3);
		logicalAnd1.add(comp4);
		
		Condition condi2 = new Condition(logicalAnd1);
		chain.addLink(condi2);
		
		SetTask startDate = new SetTask("$startdate", "$msg[0]:date");
		chain.addLink(startDate);
/*
		ConvertDateTask cdt1 = new ConvertDateTask("$time0", "$msg[0]:date");
		chain.addLink(cdt1);

		ConvertDateTask cdt2 = new ConvertDateTask("$time1", "$msg[1]:date");
		chain.addLink(cdt2);
*/		
		SetTask setTask3 = new SetTask("$duration", "$msg[1]:nanos-$msg[0]:nanos");
		chain.addLink(setTask3);
		
		SetTask setTask4 = new SetTask("$id","$msg:id");
		chain.addLink(setTask4);

		Template template = new Template();
		template.put("INFO", Template.STRING, "Results for time delta");
		template.put("date", Template.STRING, "$msg[0]:date");
		template.put("host", Template.STRING, "$msg[1]:hostid");
		template.put("id start", Template.INT, "$msg[0]:id");
		template.put("id end  ", Template.INT, "$msg[1]:id");
		template.put("TIME", Template.INT, "$duration");
		template.put("pipeline", Template.STRING, "$msg[1]:pipeline");

		ConsoleWriter outputWriter = new ConsoleWriter();
		EventTask eventTask = new EventTask(outputWriter, template);
		chain.addLink(eventTask);
		
		// write to database
		String insertQuery = "INSERT INTO test_events.durations(runid, name, sliceid, duration, host, loopnum, pipeline, date) values({$msg:runid}, {$msg:log}, {$msg:sliceid}, {$duration}, {$msg:hostid}, {$firstLoop}, {$msg:pipeline}, {$startdate});";
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
