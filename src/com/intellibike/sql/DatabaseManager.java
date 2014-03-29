package com.intellibike.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.intellibike.models.Column;

public class DatabaseManager {

	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static final String DB_URL = "jdbc:mysql://localhost/intellibike";
	private static final String USER = "intellibike";
	private static final String PASS = "password";

	private static final String BASE_QUERY = "SELECT %s FROM %s";
	private static final String BASE_WHERE_CLAUSE = "WHERE %s";
	private static final String BASE_INSERT = "INSERT INTO %s (%s) VALUES (%s)";

	private Connection connection = null;

	private static DatabaseManager instance;

	private DatabaseManager() throws ClassNotFoundException {
		Class.forName(JDBC_DRIVER);
	}

	public static DatabaseManager getInstance() throws ClassNotFoundException {
		if (instance == null) {
			instance = new DatabaseManager();
		}

		return instance;
	}

	public void connect() throws SQLException {
		connection = DriverManager.getConnection(DB_URL, USER, PASS);
	}

	public ResultSet query(String tableName) throws SQLException {
		return query(tableName, null, null, null);
	}

	public ResultSet query(String tableName, String whereClause, Column<?>[] whereArgs) throws SQLException {
		return query(tableName, null, whereClause, whereArgs);
	}

	public ResultSet query(String tableName, String[] selectArgs, String whereClause, Column<?>[] whereArgs) throws SQLException {
		sanityCheck(tableName);

		String selectString = "*";

		if (selectArgs != null) {
			selectString = constructSelectString(selectArgs);
		}

		String queryString = String.format(BASE_QUERY, selectString, tableName);

		PreparedStatement statement;
		if (whereClause != null && !whereClause.isEmpty()) {
			queryString += " " + String.format(BASE_WHERE_CLAUSE, whereClause);
			statement = constructParameters(whereArgs, queryString);
		} else {
			statement = connection.prepareStatement(queryString);
		}

		ResultSet results = statement.executeQuery();
//		statement.close();
		return results;

	}

	public int insert(String tableName, Column<?>[] columns) throws SQLException {
		sanityCheck(tableName);

		StringBuilder clauseBuilder = new StringBuilder();
		StringBuilder argsBuilder = new StringBuilder();

		for (int i = 0; i < columns.length; i++) {
			clauseBuilder.append(columns[i].getName());
			argsBuilder.append("?");

			if (i < columns.length - 1) {
				clauseBuilder.append(",");
				argsBuilder.append(",");
			}
		}

		String insertString = String.format(BASE_INSERT, tableName, clauseBuilder.toString(), argsBuilder.toString());
		PreparedStatement statement = constructParameters(columns, insertString);

		int recordsInserted = statement.executeUpdate();
		statement.close();
		return recordsInserted;
	}

	public void disconnect() throws SQLException {
		connection.close();
	}

	private void sanityCheck(String tableName) {
		if (connection == null) {
			throw new IllegalStateException("FATAL ERROR: Cannot run query without a database connection. Have you call connect before attempting to run this function?");
		}

		if (tableName == null || tableName.isEmpty()) {
			throw new UnsupportedOperationException("FATAL ERROR: Need to have a table name to run query");
		}
	}

	private String constructSelectString(String[] selectArgs) {
		StringBuilder selectBuilder = new StringBuilder();

		for (int i = 0; i < selectArgs.length; i++) {
			selectBuilder.append(selectArgs[i]);

			if (i < selectArgs.length - 1) {
				selectBuilder.append(',');
			}
		}

		return selectBuilder.toString();
	}

	private PreparedStatement constructParameters(Column<?>[] columns, String queryString) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(queryString);
		for (Column<?> column : columns) {
			switch (column.getType()) {
				case INT:
					statement.setInt(column.getIndex(), (Integer) column.getValue());
					break;
				case STRING:
					statement.setString(column.getIndex(), (String) column.getValue());
					break;

				case DOUBLE:
					statement.setDouble(column.getIndex(), (Double) column.getValue());
					break;

				case TIMESTAMP:
					statement.setTimestamp(column.getIndex(), (Timestamp) column.getValue());
					break;
				default:
					throw new IllegalArgumentException("Unknown Argument Type: " + column.getType());
			}
		}

		return statement;
	}

}
