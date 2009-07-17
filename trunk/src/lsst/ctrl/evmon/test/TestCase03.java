package lsst.ctrl.evmon.test;

import lsst.ctrl.evmon.Chain;
import lsst.ctrl.evmon.Condition;
import lsst.ctrl.evmon.Job;
import lsst.ctrl.evmon.LogicalAnd;
import lsst.ctrl.evmon.LogicalCompare;
import lsst.ctrl.evmon.Relation;
import lsst.ctrl.evmon.input.LsstEventReader;

public class TestCase03 {

	public TestCase03() {
	}
	
	public Job createJob() {
		Chain chain = new Chain();
		
		LogicalCompare comp1;
		LogicalCompare comp2;

		comp1 = new LogicalCompare("$msg:info", Relation.STARTS_WITH,
				"Starting pipeline");
		comp2 = new LogicalCompare("$msg:DATE", Relation.STARTS_WITH, "2008");

		LogicalAnd logicalAnd = new LogicalAnd(comp1, comp2);
		Condition cond = new Condition(logicalAnd);


		chain.addLink(cond);
		
  	    LsstEventReader reader = new LsstEventReader("monitor");
		Job job = new Job(reader, chain);
		
		return job;
	}
}