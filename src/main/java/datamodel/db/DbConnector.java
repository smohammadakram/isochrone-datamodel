package datamodel.db;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbConnector implements AutoCloseable {
	private Connection conn;

	// Constructor

	public DbConnector() {
		try {
			final DbConfiguration config = DbConfiguration.getInstance();
			Class.forName("org.postgresql.Driver");

			conn = DriverManager.getConnection(config.getConnectionString(), config.getDbUser(), config.getDbPassword());
			conn.setAutoCommit(false);
		} catch (final SQLException e) {
			System.out.println("[ERROR] Database does not exist. Please create it or change the database configuration.");
		} catch (final ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("[ERROR] Database driver was not found!");
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

	@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
	public ResultSet executeQuery(final String sql) throws SQLException {
		ResultSet result = null;
		try (final Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
			result = stmt.executeQuery(sql);
		}

		return result;
	}

	@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
	public void execute(final String sql) throws SQLException {
		try (final Statement stmt = conn.createStatement()) {
			stmt.execute(sql);
			conn.commit();
		}
	}

	@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
	public void executeBatch(final String[] sql) throws SQLException {
		try (final Statement stmt = conn.createStatement()) {
			for (final String s : sql) {
				stmt.addBatch(s);
			}

			stmt.executeBatch();
			conn.commit();
		}
	}

	@SuppressFBWarnings("SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING")
	public PreparedStatement getPreparedStatement(final String sql) throws SQLException {
		return conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	}

	public boolean executePrepared(final PreparedStatement stmt) throws SQLException {
		final boolean b = stmt.execute();
		conn.commit();

		return b;
	}

}
