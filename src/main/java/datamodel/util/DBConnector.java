package datamodel.util;

import datamodel.linknetwork.LinkEdge;
import datamodel.streetnetwork.Edge;
import datamodel.streetnetwork.Node;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DBConnector {

	private final static String MAPS_DB_LOCAL = "jdbc:postgresql://localhost:5432/spatial";
//	private final static String MAPS_DB_REMOTE = "jdbc:postgresql://maps.inf.unibz.it:5432/isochrones2014";
	private final static String MAPS_USER = "spatial";
//	private final static String MAPS_PWD_REMOTE = "AifaXub2";
	private final static String MAPS_PWD_LOCAL = "spatial";
	private static Connection conn;
	private BufferedWriter bw;

	public DBConnector() {
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(MAPS_DB_LOCAL, MAPS_USER, MAPS_PWD_LOCAL);
		} catch (final SQLException e) {
			System.out.println("[ERROR] Database \"spatial\" does not exist. Please create it.");
		} catch (final ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static Connection getConnection() {
		return conn;
	}

	public static void closeConnection() {
		try {
			conn.close();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public void insertService(final int id, final String start, final String end, final String vector, final String city) {
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("INSERT INTO time_expanded." + city
				+ "_bus_calendar(service_id, service_start_date, service_end_date, service_vector) VALUES(?,?,?,?);");
			stmt.setInt(1, id);
			stmt.setString(2, start);
			stmt.setString(3, end);
			stmt.setString(4, vector);
			stmt.execute();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean insertStreetNode(final long id, final String geom, final String city) {
		boolean result = false;
		try {
			final PreparedStatement stmt = conn.prepareStatement("INSERT INTO time_expanded." + city + "_street_nodes(node_id, node_geometry) "
				+ "VALUES (?, ST_GeomFromEWKT(?));");

			stmt.setLong(1, id);
			stmt.setString(2, geom.toString());
			result = stmt.execute();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public boolean insertMultipleStreetNodes(final Collection<Node> nodes, final String city) {
		final boolean result = false;
		try {
			String script = "";
			for (final Node rn : nodes) {
//				PreparedStatement stmt = conn.prepareStatement("DELETE FROM bz_isochrones_2014.bz_pedestrian_nodes");
//				stmt.execute();
//				stmt = conn.prepareStatement("INSERT INTO bz_isochrones_2014.bz_pedestrian_nodes (node_id, node_geometry) "
//						+ "VALUES (?, ST_GeomFromEWKT(?));");
//				stmt.setLong(1, rn.getId());
//				stmt.setString(2, rn.getGeometry().toString());
//				result = stmt.execute();
				script = "INSERT INTO time_expanded." + city + "_street_nodes (node_id, node_geometry) " + "VALUES ('" + rn.getId() + "', ST_GeomFromEWKT('"
					+ rn.getGeometry().toString() + "'))";
				bw.write(script);
				bw.write(";\n");
				bw.flush();
//				if(checkpoint % 50 == 0)
//					System.out.println("[INFO] Checkpoint " + checkpoint);
//				checkpoint++;
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public boolean insertMultipleStreetEdges(final Collection<Edge> edges, final String city) {
		final boolean result = false;
		try {
			for (final Edge anEdge : edges) {
				String script = "";
//				PreparedStatement stmt = conn.prepareStatement("DELETE FROM bz_isochrones_2014.bz_pedestrian_edges");
//				stmt.execute();
//				stmt = conn.prepareStatement("INSERT INTO bz_isochrones_2014.bz_pedestrian_edges (edge_id, edge_source, edge_destination,edge_geometry) "
//						+ "VALUES (?, ?, ?, ST_GeomFromEWKT(?));");
//				stmt.setLong(1, anEdge.getId());
//				stmt.setLong(2, anEdge.getSource());
//				stmt.setLong(3, anEdge.getDestination());
//				stmt.setString(4, anEdge.getGeometry().toString());
				script = "INSERT INTO time_expanded." + city + "_street_edges (edge_source, edge_destination,edge_geometry) " + "VALUES ('" + anEdge.getSource()
					+ "', '" + anEdge.getDestination() + "', ST_SetSRID(ST_GeomFromEWKT('" + anEdge.getGeometry().toString() + "'), 4326))";
//				result = stmt.execute();
				bw.write(script);
				bw.write(";\n");
				bw.flush();
//				if(checkpoint % 50 == 0)
//					System.out.println("[INFO] Checkpoint " + checkpoint);
//				checkpoint++;
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public int getLastPedestrianEdgeID(final String city) {
		try {
			final PreparedStatement stmt = conn.prepareStatement("SELECT edge_id FROM time_expanded." + city + "_street_edges ORDER BY edge_id;",
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			final ResultSet rs = stmt.executeQuery();
			if (rs.last()) {
				return rs.getInt("edge_id");
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public ResultSet executeSimpleQuery(final String sql) {
		//		System.out.println(sql);
		try {
			final PreparedStatement stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			//			System.out.println(stmt.toString());
			final ResultSet rs = stmt.executeQuery();
			if (rs != null) {
				return rs;
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void executeSimpleQueryNoResult(final String sql) {
		try {
			conn.prepareStatement(sql).execute();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public void openWriter(final String file, final boolean append) {
		try {
			bw = new BufferedWriter(new FileWriter(file, append));
//			System.out.println("[INFO] Stream opened.");
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public void closeWriter() {
		try {
			bw.close();
//			System.out.println("[INFO] Stream closed.");
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public void deleteClause(final String table) {
		final String script = "DELETE FROM time_expanded." + table + ";\n";
		try {
			bw.write(script);
			bw.flush();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public long getMaxStreetNodeID(final String city) {
		try {
			final PreparedStatement stmt = conn.prepareStatement("SELECT node_id FROM time_expanded." + city + "_street_nodes ORDER BY node_id DESC;",
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			final ResultSet rs = stmt.executeQuery();
			if (rs.first()) {
				return rs.getLong("node_id");
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public List<Integer> getBusNodeByGeometry(final String geom, final String city) {
		final List<Integer> result = new ArrayList<Integer>();
		try {
			final PreparedStatement stmt = conn.prepareStatement("SELECT node_id FROM time_expanded." + city + "_bus_nodes WHERE node_geometry = ST_GeomFromText(?);",
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			stmt.setString(1, geom);
			final ResultSet rs = stmt.executeQuery();
			rs.beforeFirst();
			while (rs.next()) {
				result.add(rs.getInt("node_id"));
			}

		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public boolean insertLinkEdge(final String city, final LinkEdge le) {
		boolean result = false;
		try {
			final PreparedStatement stmt = conn.prepareStatement("INSERT INTO time_expanded." + city
				+ "_links(link_source, link_source_mode, link_destination, link_destination_mode) VALUES(?,?,?,?)");
			stmt.setLong(1, le.getSource());
			stmt.setInt(2, le.getSourceMode());
			stmt.setLong(3, le.getDestination());
			stmt.setInt(4, le.getDestinationMode());
			result = stmt.execute();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public String getGeometryByNodeID(final long id, final String city) {
		String result = "";
		try {
			final PreparedStatement stmt = conn.prepareStatement("SELECT node_geometry FROM time_expanded." + city + "_street_nodes WHERE node_id = ?",
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			stmt.setLong(1, id);
			final ResultSet rs = stmt.executeQuery();
			rs.first();
			result = rs.getString("node_geometry");

		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

}
