package lsst.ctrl.evmon.test;

import lsst.ctrl.evmon.Chain;
import lsst.ctrl.evmon.Condition;
import lsst.ctrl.evmon.EventTask;
import lsst.ctrl.evmon.Job;
import lsst.ctrl.evmon.LogicalAnd;
import lsst.ctrl.evmon.LogicalCompare;
import lsst.ctrl.evmon.NormalizeMessageFilter;
import lsst.ctrl.evmon.Relation;
import lsst.ctrl.evmon.SetTask;
import lsst.ctrl.evmon.Template;
import lsst.ctrl.evmon.input.MysqlReader;
import lsst.ctrl.evmon.output.ConsoleWriter;

public class TestCase20 {

	public Job createJob() {
		String runId = "rlp1046";
		String query = "SELECT micros, id, sliceid, runid, level, log, comment, custom, hostid from logger where runid='"
				+ runId + "' and log='harness.pipeline.visit' order by micros;";

		Chain chain = new Chain();

		LogicalCompare cond1 = new LogicalCompare("$msg:log", Relation.EQUALS, "harness.pipeline.visit");
		LogicalCompare cond3 = new LogicalCompare("$msg:sliceid", Relation.EQUALS, "-1");
		LogicalCompare cond4 = new LogicalCompare("$msg:runid", Relation.EQUALS, runId);
		LogicalAnd firstAnd = new LogicalAnd(cond1, cond3);
		firstAnd.add(cond4);
		Condition firstCondition = new Condition(firstAnd);
		chain.addLink(firstCondition);

		SetTask setTask1 = new SetTask("$firstLoop", "$msg:loopnum");
		chain.addLink(setTask1);
		
		SetTask setTask2 = new SetTask("$nextLoop", "$msg:loopnum+1");
		chain.addLink(setTask2);

		LogicalCompare comp1 = new LogicalCompare("$msg:log", Relation.EQUALS, "harness.pipeline.visit");
		LogicalCompare comp2 = new LogicalCompare("$msg:loopnum", Relation.EQUALS, "$nextloop");
		LogicalCompare comp3 = new LogicalCompare("$msg:hostid", Relation.EQUALS, "$msg[0]:hostid");		
		
		LogicalAnd logicalAnd1 = new LogicalAnd(comp1, comp2);
		logicalAnd1.add(comp3);
		
		Condition cond2 = new Condition(comp3);
		chain.addLink(cond2);


		SetTask setTask3 = new SetTask("$time", "$msg[1]:micros-$msg[0]:micros");
		chain.addLink(setTask3);
		
		SetTask setTask4 = new SetTask("$id","$msg:id");
		chain.addLink(setTask4);

		Template template = new Template();
		template.put("INFO", Template.STRING, "Results for time delta");
		template.put("host", Template.STRING, "$msg[1]:hostid");
		template.put("id start", Template.INT, "$msg[0]:id");
		template.put("id end  ", Template.INT, "$msg[1]:id");
		template.put("TIME", Template.FLOAT, "$time");

		ConsoleWriter outputWriter = new ConsoleWriter();
		EventTask eventTask = new EventTask(outputWriter, template);
		chain.addLink(eventTask);
		
		MysqlReader mysqlReader = new MysqlReader("ds33.ncsa.uiuc.edu",
				"events", "srp", "LSSTdata");
		mysqlReader.setFilter(new NormalizeMessageFilter("custom", "||", "~~"));
		
		mysqlReader.setSelectString(query);
		Job job = new Job(mysqlReader, chain);

		return job;
	}
}
