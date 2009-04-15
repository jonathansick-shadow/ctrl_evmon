package lsst.ctrl.evmon.input;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import lsst.ctrl.evmon.NormalizeMessageFilter;
import lsst.ctrl.evmon.engine.MonitorMessage;


public class MysqlReader implements MessageReader {
	// TODO:  "NormalizeMessageFilter" should just be a MessageFilter object
	// with subclasses to do various things to messages.
	NormalizeMessageFilter filter = null;
	static int defaultPort = 3306;

	Connection conn = null;

	Statement statement = null;
	ResultSet resultSet = null;

	ResultSetMetaData meta = null;
	int columns = 0;
	String[] labelNames = null;
	
	String query = null;

	public MysqlReader(String host, String database, String user,
			String password) {
		this(host, database, user, password, defaultPort);
	}

	public MysqlReader(String host, String database, String user,
			String password, int port) {
		openConnection(host, database, user, password, port);
	}

	public void setSelectString(String query) {
		if (this.query != null) {
			System.err
					.println("MysqlReader: setSelectString has already been set to: \""
							+ this.query + "\" and can not be reset");
		}
		this.query = query;
	}

	public MonitorMessage getMessage() {
		if (query == null) {
			System.err
					.println("warning, query string has not been set in MysqlReader");
			return null;
		}

		if (resultSet == null) {
			try {
				statement = conn.createStatement();
				resultSet = statement.executeQuery(query);
				meta = resultSet.getMetaData();
				columns = meta.getColumnCount();
				labelNames = new String[columns];
				for (int i = 0; i < columns; i++)
					labelNames[i] = meta.getColumnName(i+1);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			String filterKey = null;
			if (filter != null)
				filterKey = filter.getKey();
			
			if (resultSet.next() == true) {

				MysqlMessage message = new MysqlMessage();
				for (int i = 0; i < columns; i++) {
					String name = labelNames[i];
					if (filter == null) {
						message.put(name, resultSet.getObject(i+1));
					} else if (name.equals(filterKey)) {
						filter.normalize(message, (String)resultSet.getObject(i+1));
					} else {
						message.put(name, resultSet.getObject(i+1));						
					}
				}
				return message;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			statement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return null;
	}

	public void setFilter(NormalizeMessageFilter filter) {
		this.filter = filter;
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
}
