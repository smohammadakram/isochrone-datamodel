package datamodel.impl.link;

import datamodel.db.DbConnector;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.postgis.PGgeometry;

@SuppressFBWarnings(
	value = {"SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE", "SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING"},
	justification = "Since we need to fill in the name of the city as table name prefix this can not be done in another way"
)
public class LinkNetwork {

	private static final Logger LOGGER = LogManager.getLogger(LinkNetwork.class);
	private DbConnector db;
	private String city;
	private List<PGgeometry> busNodes;
	private Map<String, String> nearestEdge;
	private Map<String, PointLocation> pointLocation;
	private Map<Long, String> additionalNodes;
	private List<LinkEdge> linkEdges;

	// Constructor

	public LinkNetwork(final DbConnector db, final String city) {
		this.db = db;
		this.city = city;

		busNodes = new ArrayList<>();
		nearestEdge = new HashMap<>();
		pointLocation = new HashMap<>();
		additionalNodes = new HashMap<>();
		linkEdges = new ArrayList<>();
	}

	// Public methods

	public void performMapping() throws SQLException {
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

	private void buildInterBusLinks() throws SQLException {
		LOGGER.info("Building inter-bus nodes links...");

		final String query = LinkQuery.getInterBusLinks(city);
		try (
			final Statement stmt = db.getStatement();
			final ResultSet rs = stmt.executeQuery(query);
		) {
			while (rs.next()) {
				LinkEdge le = new LinkEdge(rs.getInt("id_1"), 0, rs.getInt("id_2"), 0);
				linkEdges.add(le);
				le = new LinkEdge(rs.getInt("id_2"), 0, rs.getInt("id_1"), 0);
				linkEdges.add(le);
			}
		}

		LOGGER.info("Done.");
	}

	private void fillBusNodes() throws SQLException {
		LOGGER.info("Extracting bus nodes...");

		final String query = LinkQuery.getBusNodes(city);
		try (
			final Statement stmt = db.getStatement();
			final ResultSet rs = stmt.executeQuery(query);
		) {
			while (rs.next()) {
				busNodes.add(new PGgeometry(PGgeometry.geomFromString(rs.getString("n_geometry"))));
			}
		}

		LOGGER.info("Done.");
	}

	private void fillIntersectedPoints() throws SQLException {
		LOGGER.info("Extracting new points...");
		if (pointLocation.isEmpty()) {
			throw new IllegalStateException("Fill the point locations before filling additional nodespriv");
		}

		long maxStreetID = getMaxStreetNodeID();

		for (final Entry<String, PointLocation> entry : pointLocation.entrySet()) {
			final String s = entry.getKey();
			final PointLocation pl = entry.getValue();
			final String query = LinkQuery.getIntersectedPoints(city, pl.getLocation(), pl.getEdgeGeom());
			try (
				final Statement stmt = db.getStatement();
				final ResultSet rs = stmt.executeQuery(query);
			) {
				while (rs.next()) {
					//new node for link table
					final PGgeometry geom = new PGgeometry(PGgeometry.geomFromString(rs.getString("geom")));
					//<key=node, value=edge>
					maxStreetID++;
					additionalNodes.put(maxStreetID, pl.getEdgeGeom());
					insertStreetNode(maxStreetID, geom.toString());
					LinkEdge le;
					for (final Integer i : getBusNodeByGeometry(s)) {
						le = new LinkEdge(maxStreetID, 1, i, 0);
						linkEdges.add(le);
						le = new LinkEdge(i, 0, maxStreetID, 1);
						linkEdges.add(le);
					}
				}
			}

		}
		LOGGER.info("Done.");
	}

	private void fillNearestEdges() throws SQLException {
		LOGGER.info("Extracting nearest edges...");
		if (busNodes.isEmpty()) {
			throw new IllegalStateException("Fill the bus nodes before tryong to get nearest street edges");
		}

		for (final PGgeometry geom : busNodes) {
			final String query = LinkQuery.getNearestEdge(geom.toString(), city);
			try (
				final Statement stmt = db.getStatement();
				final ResultSet rs = stmt.executeQuery(query);
			) {
				if (rs.first()) {
					nearestEdge.put(geom.toString(), rs.getString("edge_geometry"));
				}
			}
		}
		LOGGER.info("Done.");
	}

	private void fillPointLocations() throws SQLException {
		LOGGER.info("Extracting point location...");
		if (nearestEdge.isEmpty()) {
			throw new IllegalStateException("Fill the nearest edges before trying to get point locations");
		}

		for (final Entry<String, String> entry : nearestEdge.entrySet()) {
			final String s = entry.getKey();
			final String nEdge = entry.getValue();
			final String query = LinkQuery.getPointLocation(city, nEdge, s);
			try (
				final Statement stmt = db.getStatement();
				final ResultSet rs = stmt.executeQuery(query);
			) {
				if (rs.first()) {
					final PointLocation pl = new PointLocation(s, rs.getFloat("p_loc"), rs.getString("edge_geometry"));
					pointLocation.put(s, pl);
				}
			}
		}
		LOGGER.info("Done.");
	}

	private List<Integer> getBusNodeByGeometry(final String geom) throws SQLException {
		final List<Integer> result = new ArrayList<Integer>();
		try (final PreparedStatement stmt = db.getPreparedStatement(LinkQuery.getBusNodeByGeom(city))) {
			stmt.setString(1, geom);
			try (final ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					result.add(rs.getInt("node_id"));
				}
			}
		}

		return result;
	}

	private long getLastPedestrianEdgeID() throws SQLException {
		long result = -1L;
		final String query = LinkQuery.getLastPedestrainEdgeId(city);
		try (
			final Statement stmt = db.getStatement();
			final ResultSet rs = stmt.executeQuery(query);
		) {
			if (rs.first()) {
				result = rs.getLong("edge_id");
			}
		}

		return result;
	}

	private Map<Long, Integer> getMapCount(final String query, final String... queries) throws SQLException {
		final Map<Long, Integer> result = new HashMap<Long, Integer>();
		try (
			final Statement stmt = db.getStatement();
			final ResultSet rs = stmt.executeQuery(query);
		) {
			while (rs.next()) {
				result.put(rs.getLong("node_id"), rs.getInt("cnt"));
			}
		}

		for (final String q : queries) {
			try (
				final Statement stmt = db.getStatement();
				final ResultSet rs = stmt.executeQuery(q);
			) {
				while (rs.next()) {
					if (result.get(rs.getLong("node_id")) != null) {
						final int oldDegree = result.get(rs.getLong("node_id"));
						result.replace(rs.getLong("node_id"), rs.getInt("cnt") + oldDegree);
					} else {
						result.put(rs.getLong("node_id"), rs.getInt("cnt"));
					}
				}
			}
		}

		return result;
	}

	private long getMaxStreetNodeID() throws SQLException {
		long result = -1L;
		final String query = LinkQuery.getMaxStreetNodeId(city);
		try (
			final Statement stmt = db.getStatement();
			final ResultSet rs = stmt.executeQuery(query);
		) {
			if (rs.first()) {
				result = rs.getLong("node_id");
			}
		}

		return result;
	}

	private String getNodeGeometry(final long id) throws SQLException {
		String result = "";
		try (final PreparedStatement stmt = db.getPreparedStatement(LinkQuery.getNodeGeometry(city))) {
			stmt.setLong(1, id);
			try (final ResultSet rs = stmt.executeQuery()) {
				if (rs.first()) {
					result = rs.getString("node_geometry");
				}
			}
		}

		return result;
	}

	private boolean insertLinkEdge(final LinkEdge le) throws SQLException {
		boolean result = false;
		try (final PreparedStatement stmt = db.getPreparedStatement(LinkQuery.getInsertLinkEdge(city))) {
			// CHECKSTYLE:OFF MagicNumber
			stmt.setLong(1, le.getSource());
			stmt.setInt(2, le.getSourceMode());
			stmt.setLong(3, le.getDestination());
			stmt.setInt(4, le.getDestinationMode());
			// CHECKSTYLE:ON MagicNumber

			result = stmt.execute();
			db.commit();
		}

		return result;
	}

	private void insertLinkEdges() throws SQLException {
		LOGGER.info("Link count: " + linkEdges.size());
		LOGGER.info("Adding links...");
		for (final LinkEdge le : linkEdges) {
			insertLinkEdge(le);
		}
		LOGGER.info("Done.");
	}

	private boolean insertStreetNode(final long id, final String geom) throws SQLException {
		boolean result = false;
		try (final PreparedStatement stmt = db.getPreparedStatement(LinkQuery.getInsertStreetNode(city))) {
			stmt.setLong(1, id);
			stmt.setString(2, geom);

			result = stmt.execute();
			db.commit();
		}

		return result;
	}

	private void updateBusNodes() throws SQLException {
		LOGGER.info("Updating bus nodes in/out degree...");

		// update indegree of bus nodes
		final Map<Long, Integer> inDegree = getMapCount(LinkQuery.getBusNodesLinkIndegree(city), LinkQuery.getBusNodesStreetIndegree(city));
		for (final Entry<Long, Integer> e : inDegree.entrySet()) {
			db.execute(LinkQuery.getBusNodesUpdateIndegree(city, e));
		}

		// update outdegree of bus nodes
		final Map<Long, Integer> outDegree = getMapCount(LinkQuery.getBusNodesLinkOutdegree(city), LinkQuery.getBusNodesStreetOutdegree(city));
		for (final Entry<Long, Integer> e : outDegree.entrySet()) {
			db.execute(LinkQuery.getBusNodesUpdateOutdegree(city, e));
		}

		LOGGER.info("Done.");
	}

	private void updateStreetEdges() throws SQLException {
		LOGGER.info("Updating street edges...");

		long lastIndex = getLastPedestrianEdgeID();
		for (final Entry<Long, String> entry : additionalNodes.entrySet()) {
			final Long l = entry.getKey();
			final String v = entry.getValue();
			final String geom = getNodeGeometry(l);
			final String query = LinkQuery.getStreetUpdateSelect(city, entry.getValue());
			try (
				final Statement stmt = db.getStatement();
				final ResultSet rs = stmt.executeQuery(query);
			) {
				while (rs.next()) {
					final long eId = rs.getLong("edge_id");
					final long eDest = rs.getLong("edge_destination");
					db.execute(LinkQuery.getStreetUpdateUpdate(city, v, geom, l, eId));
					db.execute(LinkQuery.getStreetUpdateInsert(city, v, geom, l, eDest, ++lastIndex));
				}
			}
		}

		LOGGER.info("Done.");
	}

	private void updateStreetNodes() throws SQLException {
		LOGGER.info("Updating street nodes in/out degree...");

		// update indegree of street nodes
		final Map<Long, Integer> inDegree = getMapCount(LinkQuery.getStreetNodesLinkIndegree(city), LinkQuery.getStreetNodesStreetIndegree(city));
		for (final Entry<Long, Integer> e : inDegree.entrySet()) {
			db.execute(LinkQuery.getStreetNodesUpdateIndegree(city, e));
		}

		// update outdegree of street nodes
		final Map<Long, Integer> outDegree = getMapCount(LinkQuery.getStreetNodesLinkOutdegree(city), LinkQuery.getStreetNodesStreetOutdegree(city));
		for (final Entry<Long, Integer> e : outDegree.entrySet()) {
			db.execute(LinkQuery.getStreetNodesUpdateOutdegree(city, e));
		}

		LOGGER.info("Done.");
	}

}
