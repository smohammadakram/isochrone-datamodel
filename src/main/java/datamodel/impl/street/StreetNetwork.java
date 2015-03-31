package datamodel.impl.street;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openstreetmap.osmosis.core.domain.v0_6.CommonEntityData;
import org.openstreetmap.osmosis.core.domain.v0_6.OsmUser;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;
import org.openstreetmap.osmosis.osmbinary.BinaryParser;
import org.openstreetmap.osmosis.osmbinary.Osmformat;
import org.openstreetmap.osmosis.osmbinary.Osmformat.DenseNodes;
import org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlock;
import org.openstreetmap.osmosis.osmbinary.file.BlockInputStream;
import org.postgis.LineString;
import org.postgis.PGgeometry;
import org.postgis.Point;

public class StreetNetwork extends BinaryParser {

	private static final Logger LOGGER = LogManager.getLogger(StreetNetwork.class);
	private static final int CLIENT_SRID = 4326;
	private static final Charset FILE_CS = Charset.forName("UTF-8");
	/** The magic number used to indicate no version number metadata for this entity. */
	private static final int NOVERSION = -1;
	/** The magic number used to indicate no changeset metadata for this entity. */
	private static final int NOCHANGESET = -1;
	private static final Date NODATE = new Date(-1);
	private String city;
	private Map<Long, DenseNode> denseNodes;
	private Map<Long, Long> insertedNodes;
	private int nrNodes = 0;
	private int nrEdges = 0;
	private boolean newNodes = false;
	private final File scriptEdges;
	private final File scriptNodes;

	public StreetNetwork(final String city, final String folder) {
		this.city = city;
		this.scriptEdges = new File(folder + "/" + city + "_street_edges_import.sql");
		this.scriptNodes = new File(folder + "/" + city + "_street_nodes_import.sql");

		denseNodes = new HashMap<>();
		insertedNodes = new HashMap<>();

		writeLinesToFile(scriptEdges, false, "DELETE FROM time_expanded." + city + "_street_edges");
		writeLinesToFile(scriptNodes, false, "DELETE FROM time_expanded." + city + "_street_nodes");
	}

	// Getters

	public int getNrEdges() {
		return nrEdges;
	}

	public int getNrNodes() {
		return nrNodes;
	}

	// Public methods

	@Override
	public void complete() {
		LOGGER.info("Complete!");
	}

	public Void parsePBF(final String file) throws IOException {
		try (final InputStream in = new FileInputStream(file)) {
			final BlockInputStream bIn = new BlockInputStream(in, this);
			bIn.process();
			bIn.close();
		}

		return null;
	}

	// Protected methods

	@Override
	protected void parseRelations(final List<Osmformat.Relation> relationList) {
		// ignored
	}

	@Override
	protected void parseDense(final DenseNodes nodes) {
		System.out.print("[INFO] Parsing dense nodes...");
		newNodes = true;
		long lastId = 0;
		long lastLat = 0;
		long lastLon = 0;

		for (int i = 0; i < nodes.getIdCount(); i++) {
			lastId += nodes.getId(i);
			lastLat += nodes.getLat(i);
			lastLon += nodes.getLon(i);
			final DenseNode di = new DenseNode(nodes.getDenseinfo().getVersion(i), nodes.getDenseinfo().getTimestamp(i), nodes.getDenseinfo().getChangeset(i), parseLat(lastLat), parseLon(lastLon));
			denseNodes.put(lastId, di);
		}
		LOGGER.info("Done.");
	}

	@Override
	protected void parseNodes(final List<Osmformat.Node> nodes) {
		// ignored
	}

	@Override
	protected void parseWays(final List<Osmformat.Way> wayList) {
		System.out.print("[INFO] Parsing ways...");

		final Map<Long, Way> ways = new HashMap<Long, Way>();
		for (final Osmformat.Way i : wayList) {
			boolean street = false;
			final List<Tag> tags = new ArrayList<Tag>();
			for (int j = 0; j < i.getKeysCount(); j++) {
				final String tag = getStringById(i.getKeys(j));
				final Tag t = new Tag(tag, getStringById(i.getVals(j)));
				tags.add(t);
				if (tag.equals("highway")) {
					street = true;
				}
			}

			// check if the current way is a street
			if (!street) {
				continue;
			}

			long lastId = 0;
			final List<Long> refList = i.getRefsList();
			final List<WayNode> nodes = new ArrayList<WayNode>(refList.size());
			for (final long j : refList) {
				nodes.add(new WayNode(j + lastId));
				lastId = j + lastId;
			}

			final long id = i.getId();
			final Way tmp;
			if (i.hasInfo()) {
				final Osmformat.Info info = i.getInfo();
				tmp = new Way(new CommonEntityData(id, info.getVersion(), getDate(info), OsmUser.NONE, info.getChangeset(), tags), nodes);
			} else {
				tmp = new Way(new CommonEntityData(id, NOVERSION, NODATE, OsmUser.NONE, NOCHANGESET, tags), nodes);

			}

			ways.put(id, tmp);
		}

		LOGGER.info("Done.");
		if (newNodes) {
			insertNodes2Script(getStreetNodes(ways.values()));
		}

		insertEdges2Script(addEdges(ways));
	}

	@Override
	protected void parse(final HeaderBlock header) {
		// ignored
	}

	// Private methods

	private Collection<Edge> addEdges(final Map<Long, Way> ways) {
		final Collection<Edge> edgeColection = new ArrayList<>(ways.size() * 2);
		for (final Entry<Long, Way> e : ways.entrySet()) {
			final long l = e.getKey();
			final Way way = e.getValue();
			final List<WayNode> wayNodes = way.getWayNodes();
			final Edge edge = new Edge(l, wayNodes.get(0).getNodeId(), (wayNodes.get(way.getWayNodes().size() - 1).getNodeId()), buildEdgeGeometry(wayNodes), 0);
			edgeColection.add(edge);

			Collections.reverse(wayNodes);
			final Edge edge1 = new Edge(l, wayNodes.get(0).getNodeId(), (wayNodes.get(way.getWayNodes().size() - 1).getNodeId()), buildEdgeGeometry(wayNodes), 0);
			edgeColection.add(edge1);
		}

		nrEdges += edgeColection.size();
		return edgeColection;
	}

	private PGgeometry buildEdgeGeometry(final List<org.openstreetmap.osmosis.core.domain.v0_6.WayNode> nodes) {
		final Collection<Point> points = new ArrayList<Point>(nodes.size());
		nodes.forEach(wn -> {
			final DenseNode aNode = denseNodes.get(wn.getNodeId());
			if (aNode != null) {
				points.add(new Point(aNode.getLognitude(), aNode.getLatitude()));
			}
		});

		return new PGgeometry(new LineString(points.toArray(new Point[points.size()])));
	}

	private Collection<Node> getStreetNodes(final Collection<Way> ways) {
		final Map<Long, Node> nodes = new HashMap<>();
		for (final Way way : ways) {
			// getting source and destination of the way, which are real nodes
			final Long source = way.getWayNodes().get(0).getNodeId();
			final Long destination = way.getWayNodes().get(way.getWayNodes().size() - 1).getNodeId();

			// add the source to the list if it is not there yet
			if (nodes.get(source) == null && insertedNodes.get(source) == null) {
				final DenseNode dn = denseNodes.get(source);
				final Point p = new Point(dn.getLognitude(), dn.getLatitude());
				p.setSrid(CLIENT_SRID);
				final Node aNode = new Node(source, new PGgeometry(p));
				nodes.put(source, aNode);
				insertedNodes.put(source, source);
				nrNodes++;
			}

			// same for the destination
			if (nodes.get(destination) == null && insertedNodes.get(destination) == null) {
				final DenseNode dn = denseNodes.get(destination);
				final Point p = new Point(dn.getLognitude(), dn.getLatitude());
				p.setSrid(CLIENT_SRID);
				final Node aNode = new Node(destination, new PGgeometry(p));
				nodes.put(destination, aNode);
				insertedNodes.put(destination, destination);
				nrNodes++;
			}
		}

		return nodes.values();
	}

	private void insertEdges2Script(final Collection<Edge> edgeCollection) {
		System.out.print("[INFO] Adding edges to script...");

		final Collection<String> lines = new ArrayList<>(edgeCollection.size());
		final String lineTemplate = "INSERT INTO time_expanded.%s_street_edges (edge_source, edge_destination,edge_geometry) VALUES (%d, %d, ST_SetSRID(ST_GeomFromEWKT('%s'), %d))";
		edgeCollection.forEach(e -> {
			lines.add(String.format(lineTemplate, city, e.getSource(), e.getDestination(), e.getGeometry().toString(), CLIENT_SRID));
		});

		writeLinesToFile(scriptEdges, true, lines.toArray(new String[lines.size()]));
		LOGGER.info("Done.");
	}

	private void insertNodes2Script(final Collection<Node> nodeCollection) {
		System.out.print("[INFO] Adding nodes to script...");

		final Collection<String> lines = new ArrayList<>(nodeCollection.size());
		final String lineTemplate = "INSERT INTO time_expanded.%s_street_nodes (node_id, node_geometry) VALUES (%d, ST_GeomFromEWKT('%s'))";
		nodeCollection.forEach(n -> {
			lines.add(String.format(lineTemplate, city, n.getId(), n.getGeometry().toString()));
		});

		writeLinesToFile(scriptNodes, true, lines.toArray(new String[lines.size()]));
		LOGGER.info("Done.");
	}

	// Private static methods

	private static void writeLinesToFile(final File f, final boolean append, final String... str) {
		try (final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f, append), FILE_CS))) {
			for (final String s : str) {
				bw.write(s + ";\n");
			}
			bw.flush();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

}
