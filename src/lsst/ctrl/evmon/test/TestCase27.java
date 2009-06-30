package lsst.ctrl.evmon.test;

import lsst.ctrl.evmon.Chain;
import lsst.ctrl.evmon.Condition;
import lsst.ctrl.evmon.EventTask;
import lsst.ctrl.evmon.ExceptionTask;
import lsst.ctrl.evmon.Job;
import lsst.ctrl.evmon.LogicalAnd;
import lsst.ctrl.evmon.LogicalCompare;
import lsst.ctrl.evmon.NormalizeMessageFilter;
import lsst.ctrl.evmon.Relation;
import lsst.ctrl.evmon.Template;
import lsst.ctrl.evmon.input.MysqlReader;
import lsst.ctrl.evmon.output.ConsoleWriter;
import lsst.ctrl.evmon.output.LsstEventWriter;

public class TestCase27 {

	public TestCase27() {
	}

	public Job createJob() {
		String runId = "rlp1228";
		String query = "SELECT date, timestamp, id, sliceid, runid, level, log, custom, hostid, status, pipeline from logger where runid='"
				+ runId + "' and log='harness.pipeline.visit.stage' order by timestamp;";
		
		Chain chain = new Chain();

		LogicalCompare lcA = new LogicalCompare("$msg:status", Relation.EQUALS, "start");
		LogicalCompare lcB = new LogicalCompare("$msg:stageId", Relation.NOT_EQUAL, "null");
		LogicalAnd laC =new LogicalAnd(lcA, lcB);
		Condition cond1 = new Condition(laC);
		chain.addLink(cond1);

		Template exceptionTemplate = new Template();
		exceptionTemplate.put("INFO", Template.STRING, "Problem!");

		LsstEventWriter writer = new LsstEventWriter("warning", "lsst8.ncsa.uiuc.edu");
		ExceptionTask exceptionTask = new ExceptionTask(writer, exceptionTemplate);

		LogicalCompare lc1 = new LogicalCompare("$msg:status", Relation.EQUALS, "end");
		LogicalCompare lc2 = new LogicalCompare("$msg:sliceid", Relation.EQUALS, "$msg[0]:sliceid");
		LogicalAnd logicalAnd = new LogicalAnd(lc1, lc2);
		LogicalCompare lc3 = new LogicalCompare("$msg:pipeline", Relation.EQUALS, "$msg[0]:pipeline");
		logicalAnd.add(lc3);
		Condition cond2 = new Condition(logicalAnd);
		
		
		cond2.setException(exceptionTask, 5000L);
		chain.addLink(cond2);
		
		Template template = new Template();
		template.put("MESSAGE", Template.STRING, "done");
		template.put("pipeline", Template.STRING, "$msg[0]:pipeline");
		template.put("loopnum", Template.STRING, "$msg[0]:loopnum");
		template.put("slice was", Template.STRING, "$msg[0]:sliceid");
		template.put("stageId", Template.STRING, "$msg[0]:stageId");
		template.put("start timestamp", Template.STRING, "$msg[0]:timestamp");
		template.put("end timestamp  ", Template.STRING, "$msg[1]:timestamp");
		template.put("2nd status", Template.STRING, "$msg[1]:status");
		
		// write to console
		ConsoleWriter outputWriter = new ConsoleWriter();
		EventTask eventTask = new EventTask(outputWriter, template);
		chain.addLink(eventTask);

		MysqlReader mysqlReader = new MysqlReader("lsst10.ncsa.uiuc.edu", "logs", "srp", "lsststuff");
		mysqlReader.setFilter(new NormalizeMessageFilter("custom", "=", ";"));
		
		mysqlReader.setSelectString(query);
		Job job = new Job(mysqlReader, chain);

		return job;
	}
}