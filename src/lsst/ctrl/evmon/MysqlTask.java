package lsst.ctrl.evmon;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lsst.ctrl.evmon.engine.EventStore;
import lsst.ctrl.evmon.engine.MonitorMessage;
import lsst.ctrl.evmon.output.MysqlWriter;


public class MysqlTask implements Task, Link {
	String query = null;
	MysqlWriter writer = null;
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public MysqlTask(MysqlWriter writer, String query) {
		this.writer = writer;
		this.query = query;
	}
	
	public void execute(EventStore es, MonitorMessage event) {
		writer.send(es, event, query);
	}
}