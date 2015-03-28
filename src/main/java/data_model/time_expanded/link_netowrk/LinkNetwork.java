package data_model.time_expanded.link_netowrk;

import data_model.database.DBConnector;
import data_model.time_expanded.database.TimeExpTablesDescription;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.postgis.PGgeometry;

public class LinkNetwork {

	private DBConnector db;
	private String city;
	private List<PGgeometry> bNodes;
	private Map<String, String> nearestEdge;
	private Map<String, PointLocation> pointLocation;
	private Map<Long, String> additionalNodes;
	private List<LinkEdge> linkEdges;

	public LinkNetwork(final DBConnector db, final String city) {
		this.db = db;
		this.city = city;
	}

	public void getBusNodes() {
		System.out.print("[INFO] Extracting bus nodes...");
		final ResultSet nodes = db.executeSimpleQuery("SELECT DISTINCT node_geometry AS n_geometry FROM " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_bus_nodes");
		bNodes = new ArrayList<PGgeometry>();
		try {
			nodes.beforeFirst();
			while (nodes.next()) {
				bNodes.add(new PGgeometry(PGgeometry.geomFromString(nodes.getString("n_geometry"))));
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Done.");
	}

	public void getNearestStreetEdge(){
		System.out.print("[INFO] Extracting nearest edges...");
		nearestEdge = new HashMap<String, String>();
		for (final PGgeometry geom : bNodes) {
			final ResultSet rs = db.executeSimpleQuery("SELECT edge_geometry, "
					+ "ST_Distance(edge_geometry, '" + geom.toString() + "') AS min_dist "
					+ "FROM " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_street_edges "
					+ "ORDER BY min_dist ASC");
			try {
				rs.first();
				nearestEdge.put(geom.toString(), rs.getString("edge_geometry"));
			} catch (final SQLException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Done.");
	}

	public void getPointLocation(){
		System.out.print("[INFO] Extracting point location...");
		pointLocation = new HashMap<String, PointLocation>();
		for (final String s : nearestEdge.keySet()) {
			final ResultSet rs = db.executeSimpleQuery("SELECT edge_geometry, "
					+ "ST_Line_Locate_Point('" + nearestEdge.get(s) + "', ST_GeomFromEWKT('" + s + "')) AS p_loc "
					+ "FROM " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_street_edges "
					+ "WHERE edge_geometry = '" + nearestEdge.get(s) + "'"
					+ "ORDER BY edge_geometry");
			try {
				rs.first();
				final PointLocation pl = new PointLocation(s, rs.getFloat("p_loc"), rs.getString("edge_geometry"));
				pointLocation.put(s, pl);
			} catch (final SQLException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Done.");
	}

	public void getIntersecatedPoints(){
		System.out.print("[INFO] Extracting new points...");
		long maxStreetID = db.getMaxStreetNodeID(city);
		additionalNodes = new HashMap<Long, String>();
		linkEdges = new ArrayList<LinkEdge>();
		for (final String s : pointLocation.keySet()) {
			final PointLocation pl = pointLocation.get(s);
//			ResultSet rs = db.executeSimpleQuery("SELECT ST_LineInterpolatePoint('" + pl.getEdgeGeom() + "', " + pl.getLocation() + ") AS geom "
			final ResultSet rs = db.executeSimpleQuery("SELECT ST_LineInterpolatePoint(edge_geometry, " + pl.getLocation() + ") AS geom "
					+ "FROM " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_street_edges "
					+ "WHERE edge_geometry = '" + pl.getEdgeGeom() + "'");
			try {
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

	public void builtInterBusLinks(){
		System.out.print("[INFO] Building inter-bus nodes links...");
		try {
			final ResultSet rs = db.executeSimpleQuery("SELECT bn1.node_id AS id_1, bn2.node_id AS id_2 "
					+ "FROM " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_bus_nodes bn1 JOIN  " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_bus_nodes bn2 "
							+ "ON bn1.node_geometry = bn2.node_geometry AND bn1.node_id != bn2.node_id");
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

	public void insertLinkEdges(){
		System.out.println("[INFO] Links: " + linkEdges.size());
		System.out.print("[INFO] Adding links...");
		for (final LinkEdge le : linkEdges) {
			db.insertLinkEdge(city, le);
		}
		System.out.println("Done.");
	}

//	public void createLinkNode(String city){
//		long lastIndex = db.getLastPedestrianNodeID(city);
//		ResultSet rs = db.executeSimpleQuery("SELECT node_id, ST_AsEWKT(ST_SetSRID(st_line_interpolate_point, 4326)) "
//				+ "FROM " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_bus_to_ped_coords_interpolate;");
//		try {
//			String sql = "";
//			if(rs.first()){
//				while(rs.next()){
//					System.out.println((String) rs.getObject(2));
//					System.out.println(++lastIndex);
//					sql = "INSERT INTO " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_bz_street_nodes(node_id, node_geometry) "
//							+ "VALUES('" + ++lastIndex + "', ST_SetSRID(ST_GeomFromEWKT('" + ((String) rs.getObject(2)) + "'), 4326));";
//					System.out.println(sql);
//					db.executeSimpleQuery(sql);
//					db.executeSimpleQuery("UPDATE " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_street_nodes "
//							+ "SET node_in_degree = '1', node_out_degree = '1' "
//							+ "WHERE node_in_degree IS NULL AND node_out_degree IS NULL; ");
//				}
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}

	public void updateStreetEdges(){
		System.out.print("[INFO] Updating street edges...");
		int lastIndex = db.getLastPedestrianEdgeID(city);
		try {
			for (final Long l : additionalNodes.keySet()) {
				final ResultSet rs = db.executeSimpleQuery("SELECT edge_id, edge_source, edge_destination FROM " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_street_edges "
						+ "WHERE edge_geometry = '" + additionalNodes.get(l) + "'");

				rs.beforeFirst();
				while (rs.next()) {
					final String geom = db.getGeometryByNodeID(l, city);
					String sql = "UPDATE " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_street_edges "
							+ "SET edge_geometry = ST_Snap('" + additionalNodes.get(l) + "', '" + geom + "', 1), "
							+ "edge_destination = '" + l + "', "
							+ "edge_length = ST_Length(ST_Snap('" + additionalNodes.get(l) + "', '" + geom + "', 1)) "
							+ "WHERE edge_id = '" + rs.getLong("edge_id") + "';";
					db.executeSimpleQueryNoResult(sql);
					sql = "INSERT INTO " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_street_edges (edge_id, edge_source, edge_destination, edge_length, edge_geometry) "
							+ "VALUES('" + ++lastIndex + "', '" + l + "', '" + rs.getLong(3) + "', "
							+ "ST_Length(ST_Difference('" + additionalNodes.get(l) + "', ST_Snap('" + additionalNodes.get(l) + "', '" + geom + "', 1))), "
									+ "ST_Difference('" + additionalNodes.get(l) + "', ST_Snap('" + additionalNodes.get(l) + "', '" + geom + "', 1)));";
					db.executeSimpleQueryNoResult(sql);
				}
			}
//			System.out.println("Retrieving data...");
//		ResultSet rs = db.executeSimpleQuery("SELECT edge_id, edge_source, edge_destination, bus_ped_node_id, ST_AsEWKT(ST_SetSRID(edge_geometry, 4326), ST_AsEWKT(ST_SetSRID(node_geometry, 4326)) "
//				+ "FROM " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_data_for_edges_snap;");
//		if(rs.first())
//			while(rs.next()){
//				String edgeGeometry = (String) rs.getObject(5);
//				String nodeGeometry = (String) rs.getObject(6);
//				System.out.println(rs.toString());
//				System.out.println("Updating old edges...");
//				db.executeSimpleQuery("UPDATE " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_street_edges "
//						+ "SET edge_geometry = ST_Snap(ST_SetSRID(ST_GeomFromEWKT('" + edgeGeometry + "'), 4326),  ST_SetSRID(ST_GeomFromEWKT('" + nodeGeometry + "'), 4326), 1), "
//						+ "edge_destination = '" + rs.getInt(3) + "', "
//						+ "edge_length = ST_Length(ST_Snap(ST_SetSRID(ST_GeomFromEWKT('" + edgeGeometry + "'), 4326), ST_SetSRID(ST_GeomFromEWKT('" + nodeGeometry + "'), 4326), 1)) "
//						+ "WHERE edge_id = '" + rs.getInt(1) + "';");
//				System.out.println("Inserting new edges...");
//				db.executeSimpleQuery("INSERT INTO " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_street_edges (edge_id, edge_source, edge_destination, edge_length, edge_geometry) "
//						+ "VALUES('" + ++lastIndex + "', '" + rs.getInt(4) + "', '" + rs.getInt(3) + "', "
//								+ "ST_Length(ST_Difference('" + edgeGeometry + "', ST_Snap(ST_SetSRID(ST_GeomFromEWKT('" + edgeGeometry + "'), 4326), ST_SetSRID(ST_GeomFromEWKT('" + nodeGeometry + "'), 4326), 1))), "
//										+ "ST_Difference('" + edgeGeometry + "', ST_Snap(ST_SetSRID(ST_GeomFromEWKT('" + edgeGeometry + "'), 4326), ST_SetSRID(ST_GeomFromEWKT('" + nodeGeometry + "'), 4326), 1)));");
//			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Done.");
	}


	public void updateStreetNodes(){
		System.out.print("[INFO] Updating street nodes in/out degree...");

		final Map<Long, Integer> inDegree = new HashMap<Long, Integer>();
		final Map<Long, Integer> outDegree = new HashMap<Long, Integer>();
		try{
			// updating in-degree from street edges
			ResultSet rs = db.executeSimpleQuery("SELECT sn.node_id, count(edge_destination) AS in_d\n"
					+ "FROM  " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_street_nodes sn JOIN " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_street_edges se\n"
					+ "\tON sn.node_id = se.edge_destination\n"
					+ "GROUP BY sn.node_id, se.edge_destination\n"
					+ "ORDER BY sn.node_id");
			rs.beforeFirst();
			while (rs.next()) {
				inDegree.put(rs.getLong("node_id"), rs.getInt("in_d"));
			}

			// updating in-degree from link edges
			rs = db.executeSimpleQuery("SELECT sn.node_id, count(link_destination) AS in_d\n"
					+ "FROM  " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_street_nodes sn JOIN " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_links l\n"
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
					+ "FROM  " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_street_nodes sn JOIN " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_street_edges se\n"
					+ "\tON sn.node_id = se.edge_source\n"
					+ "GROUP BY sn.node_id, se.edge_source\n"
					+ "ORDER BY sn.node_id");
			rs.beforeFirst();
			while (rs.next()) {
				outDegree.put(rs.getLong("node_id"), rs.getInt("out_d"));
			}

			// updating out-degree from link edges
			rs = db.executeSimpleQuery("SELECT sn.node_id, count(link_source) AS out_d\n"
					+ "FROM " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_street_nodes sn JOIN " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_links l\n"
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
				db.executeSimpleQueryNoResult("UPDATE  " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_street_nodes sn SET node_in_degree = " + inDegree.get(l) + " WHERE node_id = " + l);
			}
			for (final Long l : outDegree.keySet()) {
				db.executeSimpleQueryNoResult("UPDATE  " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_street_nodes sn SET node_out_degree = " + outDegree.get(l) + " WHERE node_id = " + l);
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Done.");
	}

	public void updateBusNodes(){
		System.out.print("[INFO] Updating bus nodes in/out degree...");

		final Map<Integer, Integer> inDegree = new HashMap<Integer, Integer>();
		final Map<Integer, Integer> outDegree = new HashMap<Integer, Integer>();
		try {
			// updating in-degree from street edges
			ResultSet rs = db.executeSimpleQuery("SELECT bn.node_id, count(edge_destination) AS in_d\n"
					+ "FROM  " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_bus_nodes bn JOIN " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_bus_edges be\n"
					+ "\tON bn.node_id = be.edge_destination\n"
					+ "GROUP BY bn.node_id, be.edge_destination\n"
					+ "ORDER BY bn.node_id");
			rs.beforeFirst();
			while (rs.next()) {
				inDegree.put(rs.getInt("node_id"), rs.getInt("in_d"));
			}

			// updating in-degree from link edges
			rs = db.executeSimpleQuery("SELECT bn.node_id, count(link_destination) AS in_d\n"
					+ "FROM  " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_bus_nodes bn JOIN " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_links l\n"
					+ "\tON bn.node_id = l.link_destination\n"
					+ "WHERE l.link_destination_mode = 0\n"
					+ "GROUP BY bn.node_id, l.link_destination\n"
					+ "ORDER BY bn.node_id");
			rs.beforeFirst();
			while (rs.next()) {
				if (inDegree.get(rs.getLong("node_id")) != null) {
					final int oldDegree = inDegree.get(rs.getInt("node_id"));
					inDegree.replace(rs.getInt("node_id"), rs.getInt("in_d") + oldDegree);
				} else {
					inDegree.put(rs.getInt("node_id"), rs.getInt("in_d"));
				}
			}

			// updating out-degree from street edges
			rs = db.executeSimpleQuery("SELECT bn.node_id, count(edge_source) AS out_d\n"
					+ "FROM  " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_bus_nodes bn JOIN " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_bus_edges be\n"
					+ "\tON bn.node_id = be.edge_source\n"
					+ "GROUP BY bn.node_id, be.edge_source\n"
					+ "ORDER BY bn.node_id");
			rs.beforeFirst();
			while (rs.next()) {
				outDegree.put(rs.getInt("node_id"), rs.getInt("out_d"));
			}

			// updating out-degree from link edges
			rs = db.executeSimpleQuery("SELECT bn.node_id, count(link_source) AS out_d\n"
					+ "FROM " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_bus_nodes bn JOIN " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_links l\n"
					+ "\tON bn.node_id = l.link_source\n"
					+ "WHERE l.link_source_mode = 0\n"
					+ "GROUP BY bn.node_id, l.link_source\n"
					+ "ORDER BY bn.node_id");
			rs.beforeFirst();
			while (rs.next()) {
				if (outDegree.get(rs.getLong("node_id")) != null) {
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
			db.executeSimpleQueryNoResult("UPDATE  " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_bus_nodes sn SET node_in_degree = " + inDegree.get(i) + " WHERE node_id = " + i);
		}
		for (final Integer i : outDegree.keySet()) {
			db.executeSimpleQueryNoResult("UPDATE  " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_bus_nodes sn SET node_out_degree = " + outDegree.get(i) + " WHERE node_id = " + i);
		}
		System.out.println("Done.");
	}

	public void mapping() {
		final long start = System.currentTimeMillis();
		getBusNodes();
		getNearestStreetEdge();
		getPointLocation();
		getIntersecatedPoints();
		builtInterBusLinks();
		insertLinkEdges();
		updateStreetEdges();
		updateStreetNodes();
		updateBusNodes();
		final long end = System.currentTimeMillis();
		final DateFormat df = new SimpleDateFormat("mm:ss");
		System.out.println("[INFO] Time for building link network: " + df.format(new Date((end - start))) + " minutes");
	}

//	public static void main(String[] args){
//		LinkNetwork ln = new LinkNetwork(new DBConnector(), "mebo");
//		ln.mapping();
//	}

}
