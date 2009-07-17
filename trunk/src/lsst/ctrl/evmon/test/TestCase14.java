package lsst.ctrl.evmon.test;

import lsst.ctrl.evmon.Chain;
import lsst.ctrl.evmon.Job;
import lsst.ctrl.evmon.MysqlTask;
import lsst.ctrl.evmon.SetTask;
import lsst.ctrl.evmon.input.LsstEventReader;
import lsst.ctrl.evmon.output.MysqlWriter;

public class TestCase14 {


	public Job createJob() {

//INSERT INTO events.triggerMatchMopsPredsEvent(DATE, MICROS, VISITID) values(${date}, ${micros}, ${visitid})
		String query = "INSERT INTO events.triggerMatchMopsPredsEvent(DATE, MICROS, VISITID) values({$date}, {$msg:micros}, {$msg:PID});";

		Chain chain = new Chain();

		MysqlWriter mysqlWriter = new MysqlWriter("localhost", "events", "srp", "srp123");
		
		SetTask setTask = new SetTask("$date", "$msg:DATE");
		chain.addLink(setTask);
		
		MysqlTask mysqlTask = new MysqlTask(mysqlWriter, query);
		chain.addLink(mysqlTask);

  	    LsstEventReader reader = new LsstEventReader("monitor");
		Job job = new Job(reader, chain);

		return job;
	}

}