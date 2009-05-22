package lsst.ctrl.evmon.test;

import lsst.ctrl.evmon.Chain;
import lsst.ctrl.evmon.Condition;
import lsst.ctrl.evmon.EventTask;
import lsst.ctrl.evmon.Job;
import lsst.ctrl.evmon.Relation;
import lsst.ctrl.evmon.SetTask;
import lsst.ctrl.evmon.Template;
import lsst.ctrl.evmon.input.MysqlReader;
import lsst.ctrl.evmon.output.ConsoleWriter;

public class TestCase17 {

	public Job createJob() {
		String runId = "ktls020";
		String query = "SELECT (micros-'+str(micros)+')/1000000.0 as time, sliceid, level, log, comment, custom, hostid from logger where runid='"
				+ runId + "' and micros >= '+str(micros)+' order by micros;";

		Chain chain = new Chain();

		Condition cond1 = new Condition("$msg:comment", Relation.STARTS_WITH,
				"Starting Stage Loop");
		chain.addLink(cond1);
		Condition cond2 = new Condition("$msg:comment", Relation.STARTS_WITH,
				"Completed Stage Loop");
		chain.addLink(cond2);

		SetTask setTask = new SetTask("$time", "$msg[1]:time-$msg[0]:time");
		chain.addLink(setTask);

		Template template = new Template();
		template.put("INFO", Template.STRING, "Results for time delta");
		template.put("TIME", Template.FLOAT, "$time");

		ConsoleWriter outputWriter = new ConsoleWriter();
		EventTask eventTask = new EventTask(outputWriter, template);
		chain.addLink(eventTask);

		MysqlReader mysqlReader = new MysqlReader("ds33.ncsa.uiuc.edu",
				"events", "srp", "LSSTdata");
		mysqlReader.setSelectString(query);
		Job job = new Job(mysqlReader, chain);

		return job;
	}
}