package lsst.ctrl.evmon.test;

import lsst.ctrl.evmon.Chain;
import lsst.ctrl.evmon.Condition;
import lsst.ctrl.evmon.Job;
import lsst.ctrl.evmon.MysqlTask;
import lsst.ctrl.evmon.Relation;
import lsst.ctrl.evmon.input.LsstEventReader;
import lsst.ctrl.evmon.output.MysqlWriter;

public class TestCase15 {


	public Job createJob() {

//INSERT INTO events.triggerMatchMopsPredsEvent(DATE, MICROS, VISITID) values(${date}, ${micros}, ${visitid})
		String query = "INSERT INTO events.logger(hostid, runid, sliceid, level, log, date, comment, micros) values({$msg:hostId}, {$msg:runId}, {$msg:sliceId}, {$msg:LEVEL}, {$msg:LOG}, {$msg:DATE}, {$msg:COMMENT}, {$msg:TIMESTAMP});";

		Chain chain = new Chain();

		MysqlWriter mysqlWriter = new MysqlWriter("localhost", "events", "srp", "srp123");
		
		MysqlTask mysqlTask = new MysqlTask(mysqlWriter, query);
		chain.addLink(mysqlTask);

  	    LsstEventReader reader = new LsstEventReader("LSSTLogging", "lsst8.ncsa.uiuc.edu");
		Job job = new Job(reader, chain);

		return job;
	}

}