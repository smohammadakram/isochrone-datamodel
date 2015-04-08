package datamodel.db;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DbConnector implements AutoCloseable {
	private static final Logger LOGGER = LogManager.getLogger(DbConnector.class);
	private Connection conn;

	// Constructor

	public DbConnector() {
		try {
			final DbConfiguration config = DbConfiguration.getInstance();
			Class.forName("org.postgresql.Driver");

			conn = DriverManager.getConnection(config.getConnectionString(), config.getDbUser(), config.getDbPassword());
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
		} catch (final SQLException e) {
			LOGGER.info("[ERROR] Database does not exist. Please create it or change the database configuration.");
		} catch (final ClassNotFoundException e) {
			e.printStackTrace();
			LOGGER.info("[ERROR] Database driver was not found!");
		}
	}

	// Public methods

	@Override
	public void close() {
		try {
			conn.close();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public void execute(final String... sql) throws SQLException {
		execute(false, sql);
	}

	@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
	public void execute(final boolean useBatch, final String... sql) throws SQLException {
		if (sql == null || sql.length <= 0) {
			return;
		}

		try (final Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
			for (final String s : sql) {
				if (useBatch) {
					stmt.addBatch(s);
				} else {
					stmt.execute(s);
				}
			}

			if (useBatch) {
				stmt.executeBatch();
			}

			conn.commit();
		}
	}

	public void executeScript(final DbScript script) throws SQLException {
		final String sName = script.getName();
		LOGGER.debug("Executing script \"{}\"", sName == null ? "<unnamed>" : sName);
		LOGGER.trace(" > preparing commands");
		final String[] commandsArr = script.getCommands();
		LOGGER.trace(" > executing prepared commands");

		execute(commandsArr);
		LOGGER.debug("Done.");
	}

	@SuppressFBWarnings("SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING")
	public PreparedStatement getPreparedStatement(final String sql) throws SQLException {
		return conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	}

	public void commit() throws SQLException {
		conn.commit();
	}

	@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
	public Statement getStatement() throws SQLException {
		return conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	}

}
