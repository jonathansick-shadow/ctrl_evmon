package lsst.ctrl.evmon.test;

import lsst.ctrl.evmon.Chain;
import lsst.ctrl.evmon.Condition;
import lsst.ctrl.evmon.Job;
import lsst.ctrl.evmon.Relation;
import lsst.ctrl.evmon.input.LsstEventReader;


public class TestCase01 {

	public TestCase01() {

	}
	
	public Job createJob() {


		Chain chain = new Chain();
   	    chain.addLink(new Condition("$msg:info", Relation.STARTS_WITH, "Starting Stage"));
	
		chain.addLink(new Condition("$msg:info", Relation.STARTS_WITH, "Ending Stage"));
		
		LsstEventReader reader = new LsstEventReader("monitor");
		Job task = new Job(reader, chain);   	    
   	    return task;
	}
}
