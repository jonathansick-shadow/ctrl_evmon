package lsst.ctrl.evmon.output;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lsst.ctrl.evmon.engine.EventStore;
import lsst.ctrl.evmon.engine.MonitorMessage;

import com.mysql.jdbc.MysqlDataTruncation;


public class MysqlWriter {
	static int defaultPort = 3306;
	static String MORE = "<more>";
	static int STRING_LIMIT = 1024;
	static int TRUNCATED_SIZE = STRING_LIMIT-10;

	Connection conn = null;

	public MysqlWriter(String host, String database, String user,
			String password) {
		this(host, database, user, password, defaultPort);
	}

	public MysqlWriter(String host, String database, String user,
			String password, int port) {
		openConnection(host, database, user, password, port);
	}

	public void openConnection(String host, String database, String user,
			String password, int port) {

		String url = "jdbc:mysql://" + host + "/" + database;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, user, password);
		} catch (Exception e) {
			System.err.println("Cannot connect to database \"" + database
					+ "\" at host \"" + host + "\"");
			System.err.println(e);
		}
	}

	public void send(EventStore es, MonitorMessage event, String query) {
		int length = query.length();
		StringBuffer retVal = new StringBuffer();
		
		// this pattern matches ${name} or ${foo:blah}
		Pattern p = Pattern.compile("\\{\\$(\\w+(:\\w+)?)\\}");
		Matcher m = p.matcher(query);
		int index = 0;

		Vector<String> matchList = new Vector<String>();
		while (m.find()) {
			// System.out.println("match = "+m.group());
			String value = lookup(es, event, m.group());
			retVal.append(query, index, m.start());
			retVal.append("?");
			matchList.add(value);
			index = m.end();
		}
		if (index < length)
			retVal.append(query, index, length);
		// System.out.println("retVal ="+retVal);
		try {
			// System.out.println("statement string resolved to: "+retVal);
			PreparedStatement prep = conn.prepareStatement(retVal.toString());
			for (int i = 0; i < matchList.size(); i++)
				prep.setString(i + 1, matchList.get(i));
			// System.out.println(prep);
			int count = prep.executeUpdate();

			prep.close();
		} catch (MysqlDataTruncation dt) {
			try {
			System.err.println("Truncating statement...");
			PreparedStatement prep = conn.prepareStatement(retVal.toString());
			for (int i = 0; i < matchList.size(); i++) {
				String insertionString = matchList.get(i);
				if (insertionString.length() > STRING_LIMIT) {
					insertionString = insertionString.substring(0, TRUNCATED_SIZE) +MORE;
				}
				prep.setString(i + 1, insertionString);
			}
			int count = prep.executeUpdate();
			prep.close();
			} catch (Exception e) {
				System.err.println("2) mysqlwriter send: " + e);
				System.err.println("2) statement was: "+retVal);				
			}
		} catch (Exception e) {
			System.err.println("mysqlwriter send: " + e);
			System.err.println("statement was: "+retVal);
		}
	}

	private String lookup(EventStore es, MonitorMessage msg, String token) {
		String s = token.replace('{', ' ');
		s = s.replace('}', ' ');
		s = s.trim();
		if (s.startsWith("$msg:")) {
			String[] str = s.split(":");
			Object obj = msg.get(str[1]);
			if (obj == null)
				return null;
			String val = obj.toString();
			return val;
		}
		String val = es.lookup(s);
		return val;

	}

}
