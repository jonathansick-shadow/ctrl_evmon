package lsst.ctrl.evmon.test;

import lsst.ctrl.evmon.Chain;
import lsst.ctrl.evmon.Job;
import lsst.ctrl.evmon.MysqlTask;
import lsst.ctrl.evmon.input.MysqlReader;
import lsst.ctrl.evmon.output.MysqlWriter;

public class TestCase16 {


		public Job createJob() {

	//INSERT INTO events.triggerMatchMopsPredsEvent(DATE, MICROS, VISITID) values(${date}, ${micros}, ${visitid})
			String query = "INSERT INTO events.logger(micros, hostid, sliceid, level, log, custom, comment, runid) values({$msg:micros}, {$msg:hostid}, {$msg:sliceid}, {$msg:level}, {$msg:log}, {$msg:custom}, {$msg:comment}, {$msg:runid});";

			Chain chain = new Chain();

			MysqlWriter mysqlWriter = new MysqlWriter("localhost", "events", "srp", "srp123");
			
			MysqlTask mysqlTask = new MysqlTask(mysqlWriter, query);
			chain.addLink(mysqlTask);

	  	    MysqlReader reader = new MysqlReader("ds33.ncsa.uiuc.edu", "events", "srp", "lsstdata");
	  	    reader.setSelectString("SELECT micros, hostid, sliceid, level, log, custom, comment, runid from logger WHERE runid=\"DG0257\";");
			Job job = new Job(reader, chain);

			return job;
		}

}
