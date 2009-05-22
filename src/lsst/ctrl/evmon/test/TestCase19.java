package lsst.ctrl.evmon.test;

import lsst.ctrl.evmon.Chain;
import lsst.ctrl.evmon.ExclusionFilterTask;
import lsst.ctrl.evmon.Job;
import lsst.ctrl.evmon.AttributeSet;
import lsst.ctrl.evmon.MysqlTask;
import lsst.ctrl.evmon.input.LsstEventReader;
import lsst.ctrl.evmon.output.MysqlWriter;

public class TestCase19 {
	
	public Job createJob() {

		//INSERT INTO events.triggerMatchMopsPredsEvent(DATE, MICROS, VISITID) values(${date}, ${micros}, ${visitid})
				String query = "INSERT INTO test_events.logger(hostid, runid, sliceid, level, log, date, micros, comment, custom, status) values({$msg:hostId}, {$msg:runId}, {$msg:sliceId}, {$msg:LEVEL}, {$msg:LOG}, {$msg:DATE}, {$msg:TIMESTAMP}, {$msg:COMMENT}, {$custom}, {$msg:STATUS});";

				Chain chain = new Chain();

				MysqlWriter mysqlWriter = new MysqlWriter("ds33", "test_events", "srp", "LSSTdata");

				AttributeSet attSet = new AttributeSet();
				attSet.put("hostId");
				attSet.put("runId");
				attSet.put("sliceId");
				attSet.put("LEVEL");
				attSet.put("LOG");
				attSet.put("DATE");
				attSet.put("COMMENT");
				attSet.put("TIMESTAMP");
				attSet.put("STATUS");
				
				ExclusionFilterTask filterTask = new ExclusionFilterTask("$custom", attSet);
				chain.addLink(filterTask);
				
				MysqlTask mysqlTask = new MysqlTask(mysqlWriter, query);
				chain.addLink(mysqlTask);

		  	    LsstEventReader reader = new LsstEventReader("LSSTLogging", "lsst8.ncsa.uiuc.edu");
				Job job = new Job(reader, chain);

				return job;
			}
}
