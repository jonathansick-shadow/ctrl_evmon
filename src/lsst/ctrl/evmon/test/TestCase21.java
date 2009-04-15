package lsst.ctrl.evmon.test;

import lsst.ctrl.evmon.Chain;
import lsst.ctrl.evmon.Condition;
import lsst.ctrl.evmon.Job;
import lsst.ctrl.evmon.LogicalAnd;
import lsst.ctrl.evmon.LogicalCompare;
import lsst.ctrl.evmon.MysqlTask;
import lsst.ctrl.evmon.Relation;
import lsst.ctrl.evmon.SetTask;
import lsst.ctrl.evmon.input.LsstEventReader;
import lsst.ctrl.evmon.output.MysqlWriter;

public class TestCase21 {

	public Job createJob() {


		String insertQuery = "INSERT INTO test_events.durations(runid, name, sliceid, hostid, loopnum, istage, duration) values({$msg:runId}, {$msg:LOG}, {$msg:sliceId}, {$msg:hostId}, {$msg:loopnum}, {$firstStage}, {$duration});";

		Chain chain = new Chain();

		Condition cond1 = new Condition("$msg:COMMENT", Relation.STARTS_WITH, "Top Stage Loop");
		chain.addLink(cond1);
		
		SetTask setTask1 = new SetTask("$firstStage", "$msg:iStage");
		chain.addLink(setTask1);
		
		SetTask setTask2 = new SetTask("$nextStage", "$msg:iStage+1");
		chain.addLink(setTask2);
		
		LogicalCompare comp1 = new LogicalCompare("$msg:COMMENT", Relation.STARTS_WITH, "Top Stage Loop");
		LogicalCompare comp2 = new LogicalCompare("$msg:iStage", Relation.EQUALS, "$nextStage");
		LogicalCompare comp3 = new LogicalCompare("$msg:hostId", Relation.EQUALS, "$msg[0]:hostId");
		
		LogicalAnd logicalAnd1 = new LogicalAnd(comp1, comp2);
		logicalAnd1.add(comp3);

		Condition cond2 = new Condition(logicalAnd1);
		chain.addLink(cond2);		
		

		SetTask setTask = new SetTask("$duration", "$msg[1]:TIMESTAMP-$msg[0]:TIMESTAMP");
		chain.addLink(setTask);
		
		MysqlWriter mysqlWriter = new MysqlWriter("ds33", "test_events", "srp", "LSSTdata");
		MysqlTask mysqlTask = new MysqlTask(mysqlWriter, insertQuery);
		chain.addLink(mysqlTask);
		
  	    LsstEventReader reader = new LsstEventReader("LSSTLogging", "lsst8.ncsa.uiuc.edu");
		Job job = new Job(reader, chain);

		return job;
	}
}
