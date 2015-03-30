package datamodel.db;

import datamodel.impl.link.LinkEdge;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@SuppressFBWarnings(
	value = "SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING",
	justification = "Since we need to fill in the name of the city as table name prefix this can not be done in another way"
)
public class DbConnector {
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

	public void closeConnection() {
		try {
			conn.close();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public ResultSet executeSimpleQuery(final String sql) {
		ResultSet result = null;
		try (final PreparedStatement stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
			final ResultSet rs = stmt.executeQuery();
			result = rs;
		} catch (final SQLException e) {
			e.printStackTrace();
			result = null;
		}

		return result;
	}

	@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
	public void executeSimpleQueryNoResult(final String sql) {
		try (final Statement stmt = conn.createStatement()) {
			stmt.execute(sql);
		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public List<Integer> getBusNodeByGeometry(final String geom, final String city) {
		final String query = "SELECT node_id FROM time_expanded.%s_bus_nodes WHERE node_geometry = ST_GeomFromText(?)";

		final List<Integer> result = new ArrayList<Integer>();
		try (final PreparedStatement stmt = conn.prepareStatement(String.format(query, city), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
			stmt.setString(1, geom);
			try (final ResultSet rs = stmt.executeQuery()) {
				rs.beforeFirst();
				while (rs.next()) {
					result.add(rs.getInt("node_id"));
				}
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	public String getGeometryByNodeID(final long id, final String city) {
		final String query = "SELECT node_geometry FROM time_expanded.%s_street_nodes WHERE node_id = ?";

		String result = "";
		try (final PreparedStatement stmt = conn.prepareStatement(String.format(query, city), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
			stmt.setLong(1, id);
			try (final ResultSet rs = stmt.executeQuery()) {
				rs.first();
				result = rs.getString("node_geometry");
			}
		} catch (final SQLException e) {
			e.printStackTrace();
			result = "";
		}

		return result;
	}

	public long getLastPedestrianEdgeID(final String city) {
		final String query = "SELECT edge_id FROM time_expanded.%s_street_edges ORDER BY edge_id";

		long result = -1L;
		try (
			final PreparedStatement stmt = conn.prepareStatement(String.format(query, city), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			final ResultSet rs = stmt.executeQuery()
		) {
			if (rs.last()) {
				result = rs.getLong("edge_id");
			}
		} catch (final SQLException e) {
			e.printStackTrace();
			result = -1L;
		}

		return result;
	}

	public long getMaxStreetNodeID(final String city) {
		final String query = "SELECT node_id FROM time_expanded.%s_street_nodes ORDER BY node_id DESC";

		long result = -1L;
		try (
			final PreparedStatement stmt = conn.prepareStatement(String.format(query, city), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			final ResultSet rs = stmt.executeQuery();
		) {
			if (rs.first()) {
				result = rs.getLong("node_id");
			}
		} catch (final SQLException e) {
			e.printStackTrace();
			result = -1L;
		}

		return result;
	}

	public boolean insertLinkEdge(final String city, final LinkEdge le) {
		final String query = "INSERT INTO time_expanded.%s_links(link_source, link_source_mode, link_destination, link_destination_mode) VALUES(?,?,?,?)";

		boolean result = false;
		try (final PreparedStatement stmt = conn.prepareStatement(String.format(query, city))) {
			// CHECKSTYLE:OFF MagicNumber
			stmt.setLong(1, le.getSource());
			stmt.setInt(2, le.getSourceMode());
			stmt.setLong(3, le.getDestination());
			stmt.setInt(4, le.getDestinationMode());
			// CHECKSTYLE:ON MagicNumber

			result = stmt.execute();
		} catch (final SQLException e) {
			e.printStackTrace();
			result = false;
		}

		return result;
	}

	public void insertService(final int id, final String start, final String end, final String vector, final String city) {
		final String query = "INSERT INTO time_expanded.%s_bus_calendar(service_id, service_start_date, service_end_date, service_vector) VALUES(?,?,?,?)";

		try (final PreparedStatement stmt = conn.prepareStatement(String.format(query, city))) {
			// CHECKSTYLE:OFF MagicNumber
			stmt.setInt(1, id);
			stmt.setString(2, start);
			stmt.setString(3, end);
			stmt.setString(4, vector);
			// CHECKSTYLE:ON MagicNumber

			stmt.execute();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean insertStreetNode(final long id, final String geom, final String city) {
		final String query = "INSERT INTO time_expanded.%s_street_nodes(node_id, node_geometry) VALUES (?, ST_GeomFromEWKT(?));";

		boolean result = false;
		try (final PreparedStatement stmt = conn.prepareStatement(String.format(query, city))) {
			stmt.setLong(1, id);
			stmt.setString(2, geom);

			result = stmt.execute();
		} catch (final SQLException e) {
			e.printStackTrace();
			result = false;
		}

		return result;
	}

}
