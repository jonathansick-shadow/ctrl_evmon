package lsst.ctrl.evmon.test;

import lsst.ctrl.evmon.EventMonitor;
import lsst.ctrl.evmon.Job;

public class Launch {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		

		// TODO: rewrite this launcher
		Launch launch = new Launch(); 
		int val = Integer.parseInt(args[0]);
		
		Job job = null;
		switch (val) {
		case 1:
			TestCase01 tc1 = new TestCase01();
			job = tc1.createJob();
			break;
		case 4:
			TestCase04 tc4 = new TestCase04();
			job = tc4.createJob();
			break;
		case 5:
			TestCase05 tc5 = new TestCase05();
			job = tc5.createJob();
			break;
		case 6:
			TestCase06 tc6 = new TestCase06();
			job = tc6.createJob();
			break;
		case 7:
			TestCase07 tc7 = new TestCase07();
			job = tc7.createJob();
			break;
		case 8:
			TestCase08 tc8 = new TestCase08();
			job = tc8.createJob();
			break;

		case 12:
			TestCase12 tc12 = new TestCase12();
			job = tc12.createJob();
			break;			
		case 13:
			TestCase13 tc13 = new TestCase13();
			job = tc13.createJob();
			break;
		case 14:
			TestCase14 tc14 = new TestCase14();
			job = tc14.createJob();
			break;
		case 15:
			TestCase15 tc15 = new TestCase15();
			job = tc15.createJob();
			break;
		case 16:
			TestCase16 tc16 = new TestCase16();
			job = tc16.createJob();
			break;
		case 17:
			TestCase17 tc17 = new TestCase17();
			job = tc17.createJob();
			break;
		case 18:
			TestCase18 tc18 = new TestCase18();
			job = tc18.createJob();
			break;
		case 19:
			TestCase19 tc19 = new TestCase19();
			job = tc19.createJob();
			break;
		case 20:
			TestCase20 tc20 = new TestCase20();
			job = tc20.createJob();
			break;
		case 21:
			TestCase21 tc21 = new TestCase21();
			job = tc21.createJob();
			break;
		case 22:
			TestCase22 tc22 = new TestCase22();
			job = tc22.createJob();
			break;
		case 23:
			TestCase23 tc23 = new TestCase23();
			job = tc23.createJob();
			break;
		case 24:
			TestCase24 tc24 = new TestCase24();
			job = tc24.createJob();
			break;
		case 25:
			TestCase25 tc25 = new TestCase25();
			job = tc25.createJob();
			break;
		}
		
		if (job == null) {
			System.out.println("forgot to set up test case in main!");
			System.out.println("edit Launch.java and add that.");
			System.exit(0);
		}
		System.out.println("running test "+val);
		EventMonitor engine = new EventMonitor(job);
	
		engine.runJobs();
		System.exit(0);

	}


}