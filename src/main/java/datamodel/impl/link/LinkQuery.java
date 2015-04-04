package datamodel.impl.link;

import java.util.Locale;
import java.util.Map.Entry;

final class LinkQuery {
	private static final Locale LOCALE = Locale.ENGLISH;
	private static final String BUS_NODE_BY_GEOM = "SELECT node_id FROM time_expanded.%s_bus_nodes WHERE node_geometry = ST_GeomFromText(?)";
	private static final String BUS_NODES = "SELECT DISTINCT node_geometry AS n_geometry FROM time_expanded.%s_bus_nodes";
	private static final String BUS_NODES_LINK_INDEGREE = "SELECT bn.node_id, count(link_destination) AS cnt"
		+ " FROM time_expanded.%s_bus_nodes bn JOIN time_expanded.%<s_links l"
		+ " ON bn.node_id = l.link_destination"
		+ " WHERE l.link_destination_mode = 0"
		+ " GROUP BY bn.node_id, l.link_destination"
		+ " ORDER BY bn.node_id";
	private static final String BUS_NODES_LINK_OUTDEGREE = "SELECT bn.node_id, count(link_source) AS cnt"
		+ " FROM time_expanded.%s_bus_nodes bn JOIN time_expanded.%<s_links l"
		+ " ON bn.node_id = l.link_source"
		+ " WHERE l.link_source_mode = 0"
		+ " GROUP BY bn.node_id, l.link_source"
		+ " ORDER BY bn.node_id";
	private static final String BUS_NODES_STREET_INDEGREE = "SELECT bn.node_id, count(edge_destination) AS cnt"
		+ " FROM time_expanded.%s_bus_nodes bn JOIN time_expanded.%<s_bus_edges be"
		+ " ON bn.node_id = be.edge_destination"
		+ " GROUP BY bn.node_id, be.edge_destination"
		+ " ORDER BY bn.node_id";
	private static final String BUS_NODES_STREET_OUTDEGREE = "SELECT bn.node_id, count(edge_source) AS cnt"
		+ " FROM time_expanded.%s_bus_nodes bn JOIN time_expanded.%<s_bus_edges be"
		+ " ON bn.node_id = be.edge_source"
		+ " GROUP BY bn.node_id, be.edge_source"
		+ " ORDER BY bn.node_id";
	private static final String BUS_NODES_UPDATE_INDEGREE = "UPDATE time_expanded.%s_bus_nodes sn SET node_in_degree=%d WHERE node_id=%d";
	private static final String BUS_NODES_UPDATE_OUTDEGREE = "UPDATE time_expanded.%s_bus_nodes sn SET node_out_degree=%d WHERE node_id=%d";
	private static final String INSERT_LINK_EDGE = "INSERT INTO time_expanded.%s_links(link_source, link_source_mode, link_destination, link_destination_mode) VALUES(?,?,?,?)";
	private static final String INSERT_STREET_NODE = "INSERT INTO time_expanded.%s_street_nodes(node_id, node_geometry) VALUES (?, ST_GeomFromEWKT(?))";
	private static final String INTER_BUS_LINKS = "SELECT bn1.node_id AS id_1, bn2.node_id AS id_2"
		+ " FROM time_expanded.%s_bus_nodes bn1 JOIN time_expanded.%<s_bus_nodes bn2"
		+ " ON bn1.node_geometry = bn2.node_geometry AND bn1.node_id != bn2.node_id";
	private static final String INTERSECTED_POINTS = "SELECT ST_LineInterpolatePoint(edge_geometry, %.6f) AS geom"
		+ " FROM time_expanded.%s_street_edges"
		+ " WHERE edge_geometry='%s'";
	private static final String LAST_PEDESTRIAN_EDGE_ID = "SELECT edge_id FROM time_expanded.%s_street_edges ORDER BY edge_id DESC";
	private static final String MAX_STREET_NODE_ID = "SELECT node_id FROM time_expanded.%s_street_nodes ORDER BY node_id DESC LIMIT 1";
	private static final String NEAREST_EDGE = "SELECT edge_geometry, ST_Distance(edge_geometry, '%s') AS min_dist"
		+ " FROM time_expanded.%s_street_edges"
		+ " ORDER BY min_dist ASC"
		+ " LIMIT 1";
	private static final String NODE_GEOMETRY = "SELECT node_geometry FROM time_expanded.%s_street_nodes WHERE node_id=?";
	private static final String STREET_NODES_LINK_INDEGREE = "SELECT sn.node_id, count(link_destination) AS cnt"
		+ " FROM time_expanded.%s_street_nodes sn JOIN time_expanded.%<s_links l"
		+ " ON sn.node_id = l.link_destination"
		+ " WHERE l.link_destination_mode = 1"
		+ " GROUP BY sn.node_id, l.link_destination"
		+ " ORDER BY sn.node_id"
		+ " LIMIT 1";
	private static final String STREET_NODES_LINK_OUTDEGREE = "SELECT sn.node_id, count(link_source) AS cnt"
		+ " FROM time_expanded.%s_street_nodes sn JOIN time_expanded.%<s_links l"
		+ " ON sn.node_id = l.link_source"
		+ " WHERE l.link_source_mode = 1"
		+ " GROUP BY sn.node_id, l.link_source"
		+ " ORDER BY sn.node_id";
	private static final String STREET_NODES_STREET_INDEGREE = "SELECT sn.node_id, count(edge_destination) AS cnt"
		+ " FROM time_expanded.%s_street_nodes sn JOIN time_expanded.%<s_street_edges se"
		+ " ON sn.node_id = se.edge_destination"
		+ " GROUP BY sn.node_id, se.edge_destination"
		+ " ORDER BY sn.node_id";
	private static final String STREET_NODES_STREET_OUTDEGREE = "SELECT sn.node_id, count(edge_source) AS cnt"
		+ " FROM time_expanded.%s_street_nodes sn JOIN time_expanded.%<s_street_edges se"
		+ " ON sn.node_id = se.edge_source"
		+ " GROUP BY sn.node_id, se.edge_source"
		+ " ORDER BY sn.node_id";
	private static final String STREET_NODES_UPDATE_INDEGREE = "UPDATE time_expanded.%s_street_nodes sn SET node_in_degree=%d WHERE node_id=%d";
	private static final String STREET_NODES_UPDATE_OUTDEGREE = "UPDATE time_expanded.%s_street_nodes sn SET node_out_degree=%d WHERE node_id=%d";
	private static final String STREET_UPDATE_INSERT = "INSERT INTO time_expanded.%s_street_edges (edge_id, edge_source, edge_destination, edge_length, edge_geometry)"
		+ " VALUES(%d, %d, %d, ST_Length(ST_Difference('%s', ST_Snap('%s', '%s', 1))), ST_Difference('%s', ST_Snap('%s', '%s', 1)))";
	private static final String STREET_UPDATE_SELECT = "SELECT edge_id, edge_destination FROM time_expanded.%s_street_edges WHERE edge_geometry = '%s'";
	private static final String STREET_UPDATE_UPDATE = "UPDATE time_expanded.%s_street_edges"
		+ " SET edge_geometry = ST_Snap('%s', '%s', 1), edge_destination = %d, edge_length = ST_Length(ST_Snap('%s', '%s', 1))"
		+ " WHERE edge_id=%d";
	private static final String POINT_LOCATION = "SELECT edge_geometry, ST_Line_Locate_Point('%s', ST_GeomFromEWKT('%s')) AS p_loc"
		+ " FROM time_expanded.%s_street_edges"
		+ " WHERE edge_geometry='%s'"
		+ " ORDER BY edge_geometry"
		+ " LIMIT 1";

	// Constructor

	private LinkQuery() { };

	// Package-private static methods

	static String getBusNodeByGeom(final String city) {
		return String.format(LOCALE, BUS_NODE_BY_GEOM, city);
	}

	static String getBusNodes(final String city) {
		return String.format(LOCALE, BUS_NODES, city);
	}

	static String getBusNodesLinkIndegree(final String city) {
		return String.format(LOCALE, BUS_NODES_LINK_INDEGREE, city);
	}

	static String getBusNodesLinkOutdegree(final String city) {
		return String.format(LOCALE, BUS_NODES_LINK_OUTDEGREE, city);
	}

	static String getBusNodesStreetIndegree(final String city) {
		return String.format(LOCALE, BUS_NODES_STREET_INDEGREE, city);
	}

	static String getBusNodesStreetOutdegree(final String city) {
		return String.format(LOCALE, BUS_NODES_STREET_OUTDEGREE, city);
	}

	static String getBusNodesUpdateIndegree(final String city, final Entry<Long, Integer> e) {
		return String.format(LOCALE, BUS_NODES_UPDATE_INDEGREE, city, e.getValue(), e.getKey());
	}

	static String getBusNodesUpdateOutdegree(final String city, final Entry<Long, Integer> e) {
		return String.format(LOCALE, BUS_NODES_UPDATE_OUTDEGREE, city, e.getValue(), e.getKey());
	}

	static String getInsertLinkEdge(final String city) {
		return String.format(LOCALE, INSERT_LINK_EDGE, city);
	}

	static String getInsertStreetNode(final String city) {
		return String.format(LOCALE, INSERT_STREET_NODE, city);
	}

	static String getInterBusLinks(final String city) {
		return String.format(LOCALE, INTER_BUS_LINKS, city);
	}

	static String getIntersectedPoints(final String city, final float location, final String eGeom) {
		return String.format(LOCALE, INTERSECTED_POINTS, location, city, eGeom);
	}

	static String getLastPedestrainEdgeId(final String city) {
		return String.format(LOCALE, LAST_PEDESTRIAN_EDGE_ID, city);
	}

	static String getMaxStreetNodeId(final String city) {
		return String.format(LOCALE, MAX_STREET_NODE_ID, city);
	}

	static String getNearestEdge(final String geom, final String city) {
		return String.format(LOCALE, NEAREST_EDGE, geom, city);
	}

	static String getNodeGeometry(final String city) {
		return String.format(LOCALE, NODE_GEOMETRY, city);
	}

	static String getPointLocation(final String city, final String nEdge, final String ewkt) {
		return String.format(LOCALE, POINT_LOCATION, nEdge, ewkt, city, nEdge);
	}

	static String getStreetNodesLinkIndegree(final String city) {
		return String.format(LOCALE, STREET_NODES_LINK_INDEGREE, city);
	}

	static String getStreetNodesLinkOutdegree(final String city) {
		return String.format(LOCALE, STREET_NODES_LINK_OUTDEGREE, city);
	}

	static String getStreetNodesStreetIndegree(final String city) {
		return String.format(LOCALE, STREET_NODES_STREET_INDEGREE, city);
	}

	static String getStreetNodesStreetOutdegree(final String city) {
		return String.format(LOCALE, STREET_NODES_STREET_OUTDEGREE, city);
	}

	static String getStreetNodesUpdateIndegree(final String city, final Entry<Long, Integer> e) {
		return String.format(LOCALE, STREET_NODES_UPDATE_INDEGREE, city, e.getValue(), e.getKey());
	}

	static String getStreetNodesUpdateOutdegree(final String city, final Entry<Long, Integer> e) {
		return String.format(LOCALE, STREET_NODES_UPDATE_OUTDEGREE, city, e.getValue(), e.getKey());
	}

	static String getStreetUpdateInsert(final String city, final String v, final String geom, final long l, final long eDest, final long lastIndex) {
		return String.format(LOCALE, STREET_UPDATE_INSERT, city, lastIndex, l, eDest, v, v, geom, v, v, geom);
	}

	static String getStreetUpdateSelect(final String city, final String value) {
		return String.format(LOCALE, STREET_UPDATE_SELECT, city, value);
	}

	static String getStreetUpdateUpdate(final String city, final String v, final String geom, final long l, final long eId) {
		return String.format(LOCALE, STREET_UPDATE_UPDATE, city, v, geom, l, v, geom, eId);
	}

}
