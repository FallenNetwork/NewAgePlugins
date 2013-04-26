package org.hivebuild.newageauth;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public abstract class Database {

	protected Connection dbConn;
	protected Statement dbStatement;
	protected static final Configuration config;
	protected static final IOManager io;
	private static final HashMap<String, Database> dbSystems;

	static {
		config = NewAgeAuth.getConfiguration();
		io = NewAgeAuth.getIOManager();
		dbSystems = new HashMap<String, Database>();
	}

	protected abstract void connect() throws ClassNotFoundException, SQLException;

	protected abstract void config();

	private void setupTables() {
		createTable("player", "name varchar(255) PRIMARY KEY NOT NULL"
				+ ", ip varchar(255)"
				+ ", key varchar(255)");
	}

	public void init() {
		try {
			config();
			connect();
			dbStatement = dbConn.createStatement();
			setupTables();
		} catch (SQLException e) {
			if (config.getDebug()) e.printStackTrace();
		} catch (ClassNotFoundException e) {
			if (config.getDebug()) e.printStackTrace();
			io.sendConsoleError("Can't connect to Database! Driver couldn't be found");
		}
	}

	public void shutdown() {
		try {
			dbStatement.close();
			dbConn.close();
		}catch (SQLException e) {
			if (config.getDebug()) e.printStackTrace();
		}
	}

	public boolean createTable(String tableName, String columns) {
		if (dbStatement != null) {
			try {
				dbStatement.executeUpdate("CREATE TABLE IF NOT EXISTS " + tableName + " (" + columns + ");");
				return true;
			}catch (SQLException e) {
				if (config.getDebug()) e.printStackTrace();
				return false;
			}
		}else return false;
	}

	public ResultSet query(String sql) {
		try {
			ResultSet rs = dbStatement.executeQuery(sql);
			if (rs.isAfterLast()) return null;
			//if (!rs.first()) return null;
			return rs;
		}catch (SQLException e) {
			if (config.getDebug()) e.printStackTrace();
			return null;
		}
	}

	public String queryString(String sql) {
		try {
			ResultSet rs = dbStatement.executeQuery(sql);
			if (rs.isAfterLast()) return null;
			//if (!rs.first()) return null;
			return rs.getString(1);
		}catch (SQLException e) {
			if (config.getDebug()) e.printStackTrace();
			return "";
		}
	}

	public String queryString(String sql, String column) {
		try {
			ResultSet rs = dbStatement.executeQuery(sql);
			if (rs.isAfterLast()) return null;
			//if (!rs.first()) return null;
			return rs.getString(column);
		}catch (SQLException e) {
			if (config.getDebug()) e.printStackTrace();
			return "";
		}
	}

	public boolean queryBoolean(String sql) {
		try {
			ResultSet rs = dbStatement.executeQuery(sql);
			if (rs.isAfterLast()) return false;
			//if (!rs.first()) return false;
			return rs.getBoolean(1);
		}catch (SQLException e) {
			if (config.getDebug()) e.printStackTrace();
			return false;
		}
	}

	public boolean queryBoolean(String sql, String column) {
		try {
			ResultSet rs = dbStatement.executeQuery(sql);
			if (rs.isAfterLast()) return false;
			//if (!rs.first()) return false;
			return rs.getBoolean(column);
		}catch (SQLException e) {
			if (config.getDebug()) e.printStackTrace();
			return false;
		}
	}

	public int update(String sql) {
		try {
			return dbStatement.executeUpdate(sql);
		}catch (SQLException e) {
			if (config.getDebug()) e.printStackTrace();
			return -1;
		}
	}

	public boolean tableContains(String table, String column, String value) {
		try {
			ResultSet rs = query("SELECT COUNT(" + column + ") AS " + column + "Count FROM " + table + " WHERE " + column + "='" + value + "'");
			if (rs.isAfterLast()) return false;
			//if (!rs.first()) return false;
			if (rs.getInt(1) == 0) return false;
			else return true;
		}catch (SQLException e) {
			if (config.getDebug()) e.printStackTrace();
			return false;
		}
	}

	// Static Stuff
	public static void registerDatabaseSystem(String systemName, Database dbSystem) {
		dbSystems.put(systemName, dbSystem);
	}

	public static Database getDatabaseBySystem(String systemName) {
		return dbSystems.get(systemName);
	}

	public static int parseBoolean(boolean bool) {
		if (bool) return 1;
		else return 0;
	}
}
