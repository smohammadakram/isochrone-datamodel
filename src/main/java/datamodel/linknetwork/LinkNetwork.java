package datamodel.linknetwork;

import datamodel.util.DBConnector;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.postgis.PGgeometry;

public class LinkNetwork {

	private DBConnector db;
	private String city;
	private List<PGgeometry> busNodes;
	private Map<String, String> nearestEdge;
	private Map<String, PointLocation> pointLocation;
	private Map<Long, String> additionalNodes;
	private List<LinkEdge> linkEdges;

	// Constructor

	public LinkNetwork(final DBConnector db, final String city) {
		this.db = db;
		this.city = city;

		busNodes = new ArrayList<>();
		nearestEdge = new HashMap<>();
		pointLocation = new HashMap<>();
		additionalNodes = new HashMap<>();
		linkEdges = new ArrayList<>();
	}

	// Public methods

	public void performMapping() {
		fillBusNodes();
		fillNearestEdges();
		fillPointLocations();
		fillIntersectedPoints();
		buildInterBusLinks();
		insertLinkEdges();
		updateStreetEdges();
		updateStreetNodes();
		updateBusNodes();
	}

	// Private methods

	private void fillBusNodes() {
		System.out.print("[INFO] Extracting bus nodes...");
		final String query = "SELECT DISTINCT node_geometry AS n_geometry FROM time_expanded." + city + "_bus_nodes";
		try (final ResultSet nodes = db.executeSimpleQuery(query)) {
			nodes.beforeFirst();
			while (nodes.next()) {
				busNodes.add(new PGgeometry(PGgeometry.geomFromString(nodes.getString("n_geometry"))));
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Done.");
	}

	private void fillNearestEdges() {
		System.out.print("[INFO] Extracting nearest edges...");
		if (busNodes.isEmpty()) {
			throw new IllegalStateException("Fill the bus nodes before tryong to get nearest street edges");
		}

		final String query = "SELECT edge_geometry, "
			+ "ST_Distance(edge_geometry, '%s') AS min_dist "
			+ "FROM time_expanded." + city + "_street_edges "
			+ "ORDER BY min_dist ASC";
		for (final PGgeometry geom : busNodes) {
			try (final ResultSet rs = db.executeSimpleQuery(String.format(query, geom.toString()))) {
				rs.first();
				nearestEdge.put(geom.toString(), rs.getString("edge_geometry"));
			} catch (final SQLException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Done.");
	}

	private void fillPointLocations() {
		System.out.print("[INFO] Extracting point location...");
		if (nearestEdge.isEmpty()) {
			throw new IllegalStateException("Fill the nearest edges before trying to get point locations");
		}

		final String query = "SELECT edge_geometry, "
			+ "ST_Line_Locate_Point('%s', ST_GeomFromEWKT('%s')) AS p_loc "
			+ "FROM time_expanded.%s_street_edges "
			+ "WHERE edge_geometry = '%s'"
			+ "ORDER BY edge_geometry";

		for (final Entry<String, String> entry : nearestEdge.entrySet()) {
			final String s = entry.getKey();
			final String nEdge = entry.getValue();
			try (final ResultSet rs = db.executeSimpleQuery(String.format(query, nEdge, s, city, nEdge))) {
				rs.first();
				final PointLocation pl = new PointLocation(s, rs.getFloat("p_loc"), rs.getString("edge_geometry"));
				pointLocation.put(s, pl);
			} catch (final SQLException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Done.");
	}

	private void fillIntersectedPoints() {
		System.out.print("[INFO] Extracting new points...");
		if (pointLocation.isEmpty()) {
			throw new IllegalStateException("Fill the point locations before filling additional nodespriv");
		}

		long maxStreetID = db.getMaxStreetNodeID(city);
		final String query = "SELECT ST_LineInterpolatePoint(edge_geometry, %f) AS geom "
			+ "FROM time_expanded.%s_street_edges "
			+ "WHERE edge_geometry = '%s'";

		for (final Entry<String, PointLocation> entry : pointLocation.entrySet()) {
			final String s = entry.getKey();
			final PointLocation pl = entry.getValue();
			try (final ResultSet rs = db.executeSimpleQuery(String.format(query, pl.getLocation(), city, pl.getEdgeGeom()))) {
				rs.beforeFirst();
				while (rs.next()) {
					//new node for link table
					final PGgeometry geom = new PGgeometry(PGgeometry.geomFromString(rs.getString("geom")));
					//<key=node, value=edge>
					maxStreetID++;
					additionalNodes.put(maxStreetID, pl.getEdgeGeom());
					db.insertStreetNode(maxStreetID, geom.toString(), city);
					LinkEdge le;
					for (final Integer i : db.getBusNodeByGeometry(s, city)) {
						le = new LinkEdge(maxStreetID, 1, i, 0);
						linkEdges.add(le);
						le = new LinkEdge(i, 0, maxStreetID, 1);
						linkEdges.add(le);
					}
				}
			} catch (final SQLException e) {
				e.printStackTrace();
			}

		}
		System.out.println("Done.");
	}

	private void buildInterBusLinks() {
		System.out.print("[INFO] Building inter-bus nodes links...");
		final String query = "SELECT bn1.node_id AS id_1, bn2.node_id AS id_2 "
			+ "FROM time_expanded.%s_bus_nodes bn1 JOIN  time_expanded.%s_bus_nodes bn2 "
			+ "ON bn1.node_geometry = bn2.node_geometry AND bn1.node_id != bn2.node_id";

		try (final ResultSet rs = db.executeSimpleQuery(String.format(query, city, city))) {
			rs.beforeFirst();
			while (rs.next()) {
				LinkEdge le = new LinkEdge(rs.getInt("id_1"), 0, rs.getInt("id_2"), 0);
				linkEdges.add(le);
				le = new LinkEdge(rs.getInt("id_2"), 0, rs.getInt("id_1"), 0);
				linkEdges.add(le);
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}

		System.out.println("Done.");
	}

	private void insertLinkEdges() {
		System.out.println("[INFO] Links: " + linkEdges.size());
		System.out.print("[INFO] Adding links...");
		for (final LinkEdge le : linkEdges) {
			db.insertLinkEdge(city, le);
		}
		System.out.println("Done.");
	}

	private void updateStreetEdges() {
		System.out.print("[INFO] Updating street edges...");
		int lastIndex = db.getLastPedestrianEdgeID(city);
		final String query = "SELECT edge_id, edge_source, edge_destination FROM time_expanded.%s_street_edges "
			+ "WHERE edge_geometry = '%s'";

		for (final Entry<Long, String> entry : additionalNodes.entrySet()) {
			final Long l = entry.getKey();
			try (final ResultSet rs = db.executeSimpleQuery(String.format(query, city, entry.getValue()))) {
				rs.beforeFirst();
				while (rs.next()) {
					final String geom = db.getGeometryByNodeID(l, city);
					String sql = "UPDATE time_expanded." + city + "_street_edges "
							+ "SET edge_geometry = ST_Snap('" + additionalNodes.get(l) + "', '" + geom + "', 1), "
							+ "edge_destination = '" + l + "', "
							+ "edge_length = ST_Length(ST_Snap('" + additionalNodes.get(l) + "', '" + geom + "', 1)) "
							+ "WHERE edge_id = '" + rs.getLong("edge_id") + "';";
					db.executeSimpleQueryNoResult(sql);
					sql = "INSERT INTO time_expanded." + city + "_street_edges (edge_id, edge_source, edge_destination, edge_length, edge_geometry) "
							+ "VALUES('" + ++lastIndex + "', '" + l + "', '" + rs.getLong(3) + "', "
							+ "ST_Length(ST_Difference('" + additionalNodes.get(l) + "', ST_Snap('" + additionalNodes.get(l) + "', '" + geom + "', 1))), "
									+ "ST_Difference('" + additionalNodes.get(l) + "', ST_Snap('" + additionalNodes.get(l) + "', '" + geom + "', 1)));";
					db.executeSimpleQueryNoResult(sql);
				}
//			System.out.println("Retrieving data...");
//		ResultSet rs = db.executeSimpleQuery("SELECT edge_id, edge_source, edge_destination, bus_ped_node_id, ST_AsEWKT(ST_SetSRID(edge_geometry, 4326), ST_AsEWKT(ST_SetSRID(node_geometry, 4326)) "
//				+ "FROM time_expanded." + city + "_data_for_edges_snap;");
//		if(rs.first())
//			while(rs.next()) {
//				String edgeGeometry = (String) rs.getObject(5);
//				String nodeGeometry = (String) rs.getObject(6);
//				System.out.println(rs.toString());
//				System.out.println("Updating old edges...");
//				db.executeSimpleQuery("UPDATE time_expanded." + city + "_street_edges "
//					+ "SET edge_geometry = ST_Snap(ST_SetSRID(ST_GeomFromEWKT('" + edgeGeometry + "'), 4326),  ST_SetSRID(ST_GeomFromEWKT('" + nodeGeometry + "'), 4326), 1), "
//					+ "edge_destination = '" + rs.getInt(3) + "', "
//					+ "edge_length = ST_Length(ST_Snap(ST_SetSRID(ST_GeomFromEWKT('" + edgeGeometry + "'), 4326), ST_SetSRID(ST_GeomFromEWKT('" + nodeGeometry + "'), 4326), 1)) "
//					+ "WHERE edge_id = '" + rs.getInt(1) + "';");
//				System.out.println("Inserting new edges...");
//				db.executeSimpleQuery("INSERT INTO time_expanded." + city + "_street_edges (edge_id, edge_source, edge_destination, edge_length, edge_geometry) "
//					+ "VALUES('" + ++lastIndex + "', '" + rs.getInt(4) + "', '" + rs.getInt(3) + "', "
//					+ "ST_Length(ST_Difference('" + edgeGeometry + "', ST_Snap(ST_SetSRID(ST_GeomFromEWKT('" + edgeGeometry + "'), 4326), ST_SetSRID(ST_GeomFromEWKT('" + nodeGeometry + "'), 4326), 1))), "
//					+ "ST_Difference('" + edgeGeometry + "', ST_Snap(ST_SetSRID(ST_GeomFromEWKT('" + edgeGeometry + "'), 4326), ST_SetSRID(ST_GeomFromEWKT('" + nodeGeometry + "'), 4326), 1)));");
//			}
			} catch (final SQLException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Done.");
	}

	private void updateStreetNodes() {
		System.out.print("[INFO] Updating street nodes in/out degree...");

		final Map<Long, Integer> inDegree = new HashMap<Long, Integer>();
		final Map<Long, Integer> outDegree = new HashMap<Long, Integer>();
		final String query = "SELECT sn.node_id, count(edge_destination) AS in_d\n"
			+ "FROM  time_expanded.%s_street_nodes sn JOIN time_expanded.%s_street_edges se\n"
			+ "\tON sn.node_id = se.edge_destination\n"
			+ "GROUP BY sn.node_id, se.edge_destination\n"
			+ "ORDER BY sn.node_id";
		try {
			// updating in-degree from street edges
			ResultSet rs = db.executeSimpleQuery(String.format(query, city, city));
			rs.beforeFirst();
			while (rs.next()) {
				inDegree.put(rs.getLong("node_id"), rs.getInt("in_d"));
			}

			// updating in-degree from link edges
			rs = db.executeSimpleQuery("SELECT sn.node_id, count(link_destination) AS in_d\n"
					+ "FROM  time_expanded." + city + "_street_nodes sn JOIN time_expanded." + city + "_links l\n"
					+ "\tON sn.node_id = l.link_destination\n"
					+ "WHERE l.link_destination_mode = 1\n"
					+ "GROUP BY sn.node_id, l.link_destination\n"
					+ "ORDER BY sn.node_id");
			rs.beforeFirst();
			while (rs.next()) {
				if (inDegree.get(rs.getLong("node_id")) != null) {
					final int oldDegree = inDegree.get(rs.getLong("node_id"));
					inDegree.replace(rs.getLong("node_id"), rs.getInt("in_d") + oldDegree);
				} else {
					inDegree.put(rs.getLong("node_id"), rs.getInt("in_d"));
				}
			}

			// updating out-degree from street edges
			rs = db.executeSimpleQuery("SELECT sn.node_id, count(edge_source) AS out_d\n"
					+ "FROM  time_expanded." + city + "_street_nodes sn JOIN time_expanded." + city + "_street_edges se\n"
					+ "\tON sn.node_id = se.edge_source\n"
					+ "GROUP BY sn.node_id, se.edge_source\n"
					+ "ORDER BY sn.node_id");
			rs.beforeFirst();
			while (rs.next()) {
				outDegree.put(rs.getLong("node_id"), rs.getInt("out_d"));
			}

			// updating out-degree from link edges
			rs = db.executeSimpleQuery("SELECT sn.node_id, count(link_source) AS out_d\n"
					+ "FROM time_expanded." + city + "_street_nodes sn JOIN time_expanded." + city + "_links l\n"
					+ "\tON sn.node_id = l.link_source\n"
					+ "WHERE l.link_source_mode = 1\n"
					+ "GROUP BY sn.node_id, l.link_source\n"
					+ "ORDER BY sn.node_id");
			rs.beforeFirst();
			while (rs.next()) {
				if (outDegree.get(rs.getLong("node_id")) != null) {
					final int oldDegree = outDegree.get(rs.getLong("node_id"));
					outDegree.replace(rs.getLong("node_id"), rs.getInt("out_d") + oldDegree);
				} else {
					outDegree.put(rs.getLong("node_id"), rs.getInt("out_d"));
				}
			}
			for (final Long l : inDegree.keySet()) {
				db.executeSimpleQueryNoResult("UPDATE  time_expanded." + city + "_street_nodes sn SET node_in_degree = " + inDegree.get(l) + " WHERE node_id = " + l);
			}
			for (final Long l : outDegree.keySet()) {
				db.executeSimpleQueryNoResult("UPDATE  time_expanded." + city + "_street_nodes sn SET node_out_degree = " + outDegree.get(l) + " WHERE node_id = " + l);
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Done.");
	}

	private void updateBusNodes() {
		System.out.print("[INFO] Updating bus nodes in/out degree...");

		final Map<Integer, Integer> inDegree = new HashMap<Integer, Integer>();
		final Map<Integer, Integer> outDegree = new HashMap<Integer, Integer>();
		try {
			// updating in-degree from street edges
			ResultSet rs = db.executeSimpleQuery("SELECT bn.node_id, count(edge_destination) AS in_d\n"
					+ "FROM  time_expanded." + city + "_bus_nodes bn JOIN time_expanded." + city + "_bus_edges be\n"
					+ "\tON bn.node_id = be.edge_destination\n"
					+ "GROUP BY bn.node_id, be.edge_destination\n"
					+ "ORDER BY bn.node_id");
			rs.beforeFirst();
			while (rs.next()) {
				inDegree.put(rs.getInt("node_id"), rs.getInt("in_d"));
			}

			// updating in-degree from link edges
			rs = db.executeSimpleQuery("SELECT bn.node_id, count(link_destination) AS in_d\n"
					+ "FROM  time_expanded." + city + "_bus_nodes bn JOIN time_expanded." + city + "_links l\n"
					+ "\tON bn.node_id = l.link_destination\n"
					+ "WHERE l.link_destination_mode = 0\n"
					+ "GROUP BY bn.node_id, l.link_destination\n"
					+ "ORDER BY bn.node_id");
			rs.beforeFirst();
			while (rs.next()) {
				if (inDegree.get(rs.getInt("node_id")) != null) {
					final int oldDegree = inDegree.get(rs.getInt("node_id"));
					inDegree.replace(rs.getInt("node_id"), rs.getInt("in_d") + oldDegree);
				} else {
					inDegree.put(rs.getInt("node_id"), rs.getInt("in_d"));
				}
			}

			// updating out-degree from street edges
			rs = db.executeSimpleQuery("SELECT bn.node_id, count(edge_source) AS out_d\n"
					+ "FROM  time_expanded." + city + "_bus_nodes bn JOIN time_expanded." + city + "_bus_edges be\n"
					+ "\tON bn.node_id = be.edge_source\n"
					+ "GROUP BY bn.node_id, be.edge_source\n"
					+ "ORDER BY bn.node_id");
			rs.beforeFirst();
			while (rs.next()) {
				outDegree.put(rs.getInt("node_id"), rs.getInt("out_d"));
			}

			// updating out-degree from link edges
			rs = db.executeSimpleQuery("SELECT bn.node_id, count(link_source) AS out_d\n"
					+ "FROM time_expanded." + city + "_bus_nodes bn JOIN time_expanded." + city + "_links l\n"
					+ "\tON bn.node_id = l.link_source\n"
					+ "WHERE l.link_source_mode = 0\n"
					+ "GROUP BY bn.node_id, l.link_source\n"
					+ "ORDER BY bn.node_id");
			rs.beforeFirst();
			while (rs.next()) {
				if (outDegree.get(rs.getInt("node_id")) != null) {
					final int oldDegree = outDegree.get(rs.getInt("node_id"));
					outDegree.replace(rs.getInt("node_id"), rs.getInt("out_d") + oldDegree);
				} else {
					outDegree.put(rs.getInt("node_id"), rs.getInt("out_d"));
				}
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		for (final Integer i : inDegree.keySet()) {
			db.executeSimpleQueryNoResult("UPDATE time_expanded." + city + "_bus_nodes sn SET node_in_degree = " + inDegree.get(i) + " WHERE node_id = " + i);
		}
		for (final Integer i : outDegree.keySet()) {
			db.executeSimpleQueryNoResult("UPDATE time_expanded." + city + "_bus_nodes sn SET node_out_degree = " + outDegree.get(i) + " WHERE node_id = " + i);
		}
		System.out.println("Done.");
	}

}
