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

public class TestCase24 {

	public Job createJob() {
		Chain chain = new Chain();

		
		SetTask setTask1 = new SetTask("$firstLoop", "1");
		chain.addLink(setTask1);

		LsstEventReader reader = new LsstEventReader("LSSTLogging",
				"lsst4.ncsa.uiuc.edu");

		Job job = new Job(reader, chain);

		return job;
	}
}
