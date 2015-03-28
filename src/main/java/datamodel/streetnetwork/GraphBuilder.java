package datamodel.streetnetwork;

import datamodel.util.DBConnector;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.openstreetmap.osmosis.osmbinary.file.BlockReaderAdapter;
import org.postgis.LineString;
import org.postgis.PGgeometry;
import org.postgis.Point;

public class GraphBuilder extends BinaryParser {

	/** The magic number used to indicate no version number metadata for this entity. */
    private static final int NOVERSION = -1;
    /** The magic number used to indicate no changeset metadata for this entity. */
    private static final int NOCHANGESET = -1;
	private Map<Long, DenseNode> denseNodes;
	private Map<Long, Node> realNodes;
	private Map<Long, Long> insertedNodes;
	private Map<Long, Way> ways;
	private List<Edge> edges;
	private String outPath;
	private String city;
	private DBConnector db;
	private String file;
	private static final Date NODATE = new Date(-1);
	private int nrNodes = 0;
	private int nrEdges = 0;
	private boolean newNodes = false;
//	private List<PBFBlock> waysBlocks = new ArrayList<PBFBlock>();

	public GraphBuilder(final String file, final String outPath, final String city, final DBConnector conn){
		this.file = file;
		denseNodes = new HashMap<>();
		realNodes = new HashMap<>();
		insertedNodes = new HashMap<>();
		ways = new HashMap<>();
		edges = new ArrayList<>();
		this.outPath = outPath;
		this.city = city;
		db = conn;
	}

    public long intersect(final List<Long> outer, final List<Long> inner){
		for (final Long l : outer) {
			if (l > 0 && String.valueOf(l).length() >= 7) {
				for (final Long l1 : inner) {
					if (l1 > 0 && String.valueOf(l1).length() >= 7 && l1.equals(l)) {
						return l1;
					}
				}
			}
		}

		return -1;
    }

//	public void parseNetwork() {
//		for (PBFBlock block : waysBlock) {
//			parseWays(block.getWays());
//			getRealStreetNodes();
//		}
//	}

    public Collection<Node> getStreetNodes(){
//    	System.out.println("[INFO] Real Nodes.");
//    	realNodes = new HashMap<Long, RealNode>();
//    	System.out.println("Nodes: " + nodes);
//    	parseWays(waysBlock);
//    	List<Long> discovered = new ArrayList<Long>();
    	for (final Way way : ways.values()) {
    		//getting source and destination of the way, which are real nodes
    		final Long source = way.getWayNodes().get(0).getNodeId();
    		final Long destination = way.getWayNodes().get(way.getWayNodes().size()-1).getNodeId();

    		//add the source to the list if it is not there yet
    		if (realNodes.get(source) == null && insertedNodes.get(source) == null) {
	    		final DenseNode dn = denseNodes.get(source);
	    		final Point p = new Point(dn.getLognitude(), dn.getLatitude());
	    		p.setSrid(4326);
	    		final Node aNode = new Node(source, new PGgeometry(p));
	    		realNodes.put(source, aNode);
	    		insertedNodes.put(source, source);
//	    		discovered.add(dn.getId());
	    		nrNodes++;
    		}

    		//same for the destination
    		if (realNodes.get(destination) == null && insertedNodes.get(destination) == null) {
    			final DenseNode dn = denseNodes.get(destination);
	    		final Point p = new Point(dn.getLognitude(),dn.getLatitude());
	    		p.setSrid(4326);
	    		final Node aNode = new Node(destination, new PGgeometry(p));
	    		realNodes.put(destination, aNode);
	    		insertedNodes.put(destination, destination);
//	    		discovered.add(dn.getId());
	    		nrNodes++;
    		}
    	}

//    	System.out.println("[INFO] Real nodes: " + realNodes.values().size());
//    	removeDiscoveredNodes(discovered);
    	final Collection<Node> result = realNodes.values();
//    	denseNodes = new HashMap<Long, DenseNode>();
    	buildNodes();
    	realNodes = new HashMap<Long, Node>();
    	return result;
    }

    public void removeDiscoveredNodes(final List<Long> nodes){
    	for(final Long l : nodes) {
			denseNodes.remove(l);
		}
    }

    public void populateScript(){
    	if(newNodes){
    		buildNodes();
    	}
    	buildEdges();
    }

    public Map<Long, Node> buildNodes(){
//    	Map<Long, RealNode> realNodes = new HashMap<Long, RealNode>();
//    	Collection<RealNode> nodes = this.realNodes.values();
//		System.out.println("[INFO] Nodes: " + nodes.size());
    	System.out.print("[INFO] Bulding real nodes...");
    	final List<Node> tmp = new ArrayList<Node>();
//    	db.resetFile(outPath + "/" + city + "_street_nodes_import.sql");
		db.openWriter(outPath + "/" + city + "_street_nodes_import.sql", true);
//		db.deleteClause(city + "_street_nodes");
//		db.resetCheckpoint();
//		final int counter = 0;
		for (final Node rn : realNodes.values()) {
//			if(counter == 999){
//				counter = 0;
//				insertNodes(tmp);
//				tmp = new ArrayList<RealNode>();
//			}
//			realNodes.put(rn.getId(), rn);
			tmp.add(rn);
//			nrNodes++;
//			counter++;
		}
//		if(counter != 0)
			insertNodes(tmp);
		db.closeWriter();
		System.out.println("Done.");
		return this.realNodes;
	}

    public void insertNodes(final Collection<Node> nodes){
    	db.insertMultipleStreetNodes(nodes, city);
    }

    public List<Edge> buildEdges(){
    	System.out.print("[INFO] Building edges...");
//    	Map<Long, Edge> edges = new HashMap<Long, Edge>();
    	final List<Edge> tmp = new ArrayList<Edge>();
//    	db.resetFile(outPath + "/" + city + "_street_edges_import.sql");
    	db.openWriter(outPath + "/" + city + "_street_edges_import.sql", true);
//    	db.deleteClause(city + "_street_edges");
//    	db.resetCheckpoint();
//    	final int counter = 0;
    	for (final Edge e : edges) {
//    		if(counter == 999){
//    			insertEdges(tmp);
//    			counter = 0;
//    			tmp = new ArrayList<Edge>();
//    		}
    		tmp.add(e);
//    		nrEdges++;
//    		counter += 2;
    	}
//    	for (Long l : ways.keySet()) {
//    		if(counter == 999){
//    			insertEdges(edges);
//    			edges = new ArrayList<Edge>();
//    		}
//    		Way way = ways.get(l);
//    		List<WayNode> tmp = way.getWayNodes();
//    		Edge e = new Edge(l, tmp.get(0).getNodeId(), (tmp.get(way.getWayNodes().size()-1).getNodeId()), buildEdgeGeometry(tmp), 0);
//    		edges.add(e);
//    		Collections.reverse(tmp);
//    		e = new Edge(l, tmp.get(0).getNodeId(), (tmp.get(way.getWayNodes().size()-1).getNodeId()), buildEdgeGeometry(tmp), 0);
//    		edges.add(e);
////    		edges.put(destination, reverseEdge(edgePoint, destination, source));
//    		counter += 2;
//    	}
//    	if(counter != 0)
    		insertEdges(tmp);
    	db.closeWriter();
    	System.out.println("Done.");
    	return edges;
    }

	public void addEdges(final Set<Long> waysKeys) {
		for (final Long l : waysKeys) {
			final Way way = ways.get(l);
			final List<WayNode> tmp = way.getWayNodes();
			Edge e = new Edge(l, tmp.get(0).getNodeId(), (tmp.get(way.getWayNodes().size() - 1).getNodeId()), buildEdgeGeometry(tmp), 0);
			edges.add(e);
			Collections.reverse(tmp);
			e = new Edge(l, tmp.get(0).getNodeId(), (tmp.get(way.getWayNodes().size() - 1).getNodeId()), buildEdgeGeometry(tmp), 0);
			edges.add(e);
		}
		nrEdges += edges.size();
	}

	public Edge reverseEdge(final ArrayList<Point> points, final long id, final long source, final long destination) {
		Collections.reverse(points);
		final PGgeometry edge = new PGgeometry(new LineString(points.toArray(new Point[] {})));
		return new Edge(id, source, destination, edge, 0);
	}

	public PGgeometry buildEdgeGeometry(final List<org.openstreetmap.osmosis.core.domain.v0_6.WayNode> nodes) {
		final ArrayList<Point> points = new ArrayList<Point>();
		for (final org.openstreetmap.osmosis.core.domain.v0_6.WayNode wn : nodes) {
			final DenseNode aNode = denseNodes.get(wn.getNodeId());
			if (aNode != null) {
				final Point p = new Point(aNode.getLognitude(), aNode.getLatitude());
				points.add(p);
			}
		}
		return new PGgeometry(new LineString(points.toArray(new Point[] {})));
	}

	public void insertEdges(final Collection<Edge> edges) {
		db.insertMultipleStreetEdges(edges, city);
	}

	public String getCity() {
		return city;
	}

	public void setCity(final String city) {
		this.city = city;
	}

	public void parsePBF() {
		InputStream input;
		try {
			input = new FileInputStream(file);
			final BlockReaderAdapter brad = this;
			new BlockInputStream(input, brad).process();
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	   /** Get the osmosis object representing a the user in a given Info protobuf.
     * @param info The info protobuf.
     * @return The OsmUser object */
	OsmUser getUser(final Osmformat.Info info) {
		// System.out.println(info);
		if (info.hasUid() && info.hasUserSid()) {
			if (info.getUid() < 0) {
				return OsmUser.NONE;
			}
			return new OsmUser(info.getUid(), getStringById(info.getUserSid()));
		} else {
			return OsmUser.NONE;
		}
	}

    @Override
    protected void parseRelations(final List<Osmformat.Relation> rels) {
        if (!rels.isEmpty()) {
        	System.out.println("[INFO] Got some relations to parse.");
//			for(Relation rel : rels) {
//				System.out.println("Relation, %e, ", rel.getId(), rel.get);
//        	}
		}
    }

	@Override
	protected void parseDense(final DenseNodes nodes) {
		System.out.print("[INFO] Parsing dense nodes...");
		newNodes = true;
//    	denseNodes = new HashMap<Long, DenseNode>();
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
		System.out.println("Done.");
	}

	@Override
	protected void parseNodes(final List<Osmformat.Node> nodes) {
		for (final Osmformat.Node n : nodes) {
			System.out.printf("[INFO] Regular node, ID %d @ %.6f,%.6f\n", n.getId(), parseLat(n.getLat()), parseLon(n.getLon()));
		}
	}

    @Override
    protected void parseWays(final List<Osmformat.Way> ways) {
//		if (ways,size() > 0) {
//			PBFBlock aBlock = new PBFBlock(ways, strings);
//			waysBlocks.add(aBlock);
//		}

    	this.ways = new HashMap<Long, Way>();
    	edges = new ArrayList<Edge>();
    	System.out.print("[INFO] Parsing ways...");
    	boolean street = false;

		for (final Osmformat.Way i : ways) {
			street = false;
			final List<Tag> tags = new ArrayList<Tag>();
			for (int j = 0; j < i.getKeysCount(); j++) {
				final String tag = getStringById(i.getKeys(j));
				final Tag t = new Tag(tag, getStringById(i.getVals(j)));
				tags.add(t);
				if (tag.equals("highway")) {
					street = true;
				}
			}

             //check if the current way is a street
			if (!street) {
				continue;
			}

			long lastId = 0;
			final List<WayNode> nodes = new ArrayList<WayNode>();
			for (final long j : i.getRefsList()) {
				nodes.add(new WayNode(j + lastId));
				lastId = j + lastId;
			}

			final long id = i.getId();

			// long id, int version, Date timestamp, OsmUser user,
			// long changesetId, Collection<Tag> tags,
			// List<WayNode> wayNodes
			Way tmp;
			if (i.hasInfo()) {
				final Osmformat.Info info = i.getInfo();
				tmp = new Way(new CommonEntityData(id, info.getVersion(), getDate(info), OsmUser.NONE, info.getChangeset(), tags), nodes);
			} else {
				tmp = new Way(new CommonEntityData(id, NOVERSION, NODATE, OsmUser.NONE, NOCHANGESET, tags), nodes);

			}

			this.ways.put(id, tmp);
        }

    	System.out.println("Done.");
    	if (newNodes) {
			getStreetNodes();
		}
        addEdges(this.ways.keySet());
        buildEdges();

//		populateScript();
//		denseNodes = null;
    }

    @Override
    protected void parse(final HeaderBlock header) {
        System.out.println("[INFO] Got header block.");
    }

    @Override
	public void complete() {
        System.out.println("[INFO] Complete!");
    }

    public Map<Long, DenseNode> getAllNodes() {
    	return denseNodes;
    }

	public Map<Long, org.openstreetmap.osmosis.core.domain.v0_6.Way> getAllWays() {
		return ways;
	}

	public int getNrEdges() {
		return nrEdges;
	}

	public int getNrNodes() {
		return nrNodes;
	}
}
