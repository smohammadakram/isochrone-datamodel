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
			Class.forName("org.postgresql.Driver");
			final DbConfiguration config = DbConfiguration.getInstance();
			conn = DriverManager.getConnection(config.getConnectionString(), config.getDbUser(), config.getDbPassword());
		} catch (final SQLException e) {
			System.out.println("[ERROR] Database does not exist. Please create it or change the database configuration.");
		} catch (final ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("[ERROR] Database driver was not found!");
		}
	}


	// Getter

	public Connection getConnection() {
		return conn;
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

	@SuppressFBWarnings("SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING")
	public ResultSet executeQuery(final String sql) throws SQLException {
		ResultSet result = null;
		try (final PreparedStatement stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
			final ResultSet rs = stmt.executeQuery();
			result = rs;
		}

		return result;
	}

	@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
	public void execute(final String sql) throws SQLException {
		try (final Statement stmt = conn.createStatement()) {
			stmt.execute(sql);
		}
	}

}
