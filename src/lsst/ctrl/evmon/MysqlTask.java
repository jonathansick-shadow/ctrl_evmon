package lsst.ctrl.evmon;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lsst.ctrl.evmon.engine.EventStore;
import lsst.ctrl.evmon.engine.MonitorMessage;
import lsst.ctrl.evmon.output.MysqlWriter;


/**
 * Class MysqlTask writes queries to a MySQL database
 */
public class MysqlTask implements Task, Link {
	String query = null;
	MysqlWriter writer = null;
	// SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
    /**
     * Class constructor MysqlTask writes a query to a MySQL database, and
     * is used to insert values.  The query string can have EventMonitor variables
     * within it, and those values will be substituted at the time the MysqlTask
     * is encountered when running the Chain.
     *
     * @param writer MysqlWriter that will write the query
     * @param query is the query to send
     */
	public MysqlTask(MysqlWriter writer, String query) {
		this.writer = writer;
		this.query = query;
	}
	
    /**
     * Sends the query string to the writer, given the EventStore and current
     * message being evaluated by the EventMonitor.
     *
     * @param ce ChainEnvironment to evaluate against
     * @param msg current message to evaluate against
     */
	public void execute(EventStore es, MonitorMessage event) {
		writer.send(es, event, query);
	}
}
