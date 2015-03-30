package datamodel.streetnetwork;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openstreetmap.osmosis.core.domain.v0_6.CommonEntityData;
import org.openstreetmap.osmosis.core.domain.v0_6.OsmUser;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;
import org.openstreetmap.osmosis.osmbinary.BinaryParser;
import org.openstreetmap.osmosis.osmbinary.Osmformat;
import org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlock;
import org.openstreetmap.osmosis.osmbinary.file.BlockInputStream;
import org.openstreetmap.osmosis.osmbinary.file.BlockReaderAdapter;

public class PbfParser extends BinaryParser {
	/** The magic number used to indicate no version number metadata for this entity. */
	private static final int NOVERSION = -1;
	/** The magic number used to indicate no changeset metadata for this entity. */
	private static final int NOCHANGESET = -1;
	private String file;
	private Map<Long, DenseNode> allNodes;
	private Map<Long, org.openstreetmap.osmosis.core.domain.v0_6.Way> allWays;

	// Constructor

	public PbfParser(final String file) {
		this.file = file;
		allNodes = new HashMap<>();
		allWays = new HashMap<>();
	}

	// Public methods

	@Override
	public void complete() {
		System.out.println("[INFO] Complete!");
	}

	public Map<Long, DenseNode> getAllNodes() {
		return allNodes;
	}

	public Map<Long, org.openstreetmap.osmosis.core.domain.v0_6.Way> getAllWays() {
		return allWays;
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

	// Protected methods

	@Override
	protected void parseRelations(final List<Osmformat.Relation> rels) {
		if (!rels.isEmpty()) {
			System.out.println("[INFO] Got some relations to parse.");
//			for (Osmformat.Relation rel : rels) {
//				System.out.println("Relation, %e, ", rel.getId(), rel.get);
//			}
		}
	}

	@Override
	protected void parseDense(final Osmformat.DenseNodes nodes) {
		System.out.print("[INFO] Parsing dense nodes...");
		long lastId = 0;
		long lastLat = 0;
		long lastLon = 0;

		for (int i = 0; i < nodes.getIdCount(); i++) {
			lastId += nodes.getId(i);
			lastLat += nodes.getLat(i);
			lastLon += nodes.getLon(i);
			final DenseNode di = new DenseNode(nodes.getDenseinfo().getVersion(i), nodes.getDenseinfo().getTimestamp(i), nodes.getDenseinfo().getChangeset(i), parseLat(lastLat), parseLon(lastLon));
			allNodes.put(lastId, di);
		}

		System.out.println("Done.");
	}

	@Override
	protected void parseNodes(final List<Osmformat.Node> nodes) {
		for (final Osmformat.Node n : nodes) {
			System.out.printf("[INFO] Regular node, ID %d @ %.6f,%.6f", n.getId(), parseLat(n.getLat()), parseLon(n.getLon()));
			System.out.println();
		}
	}

	@Override
	protected void parseWays(final List<Osmformat.Way> ways) {
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
			org.openstreetmap.osmosis.core.domain.v0_6.Way tmp;
			if (i.hasInfo()) {
				final Osmformat.Info info = i.getInfo();
				tmp = new org.openstreetmap.osmosis.core.domain.v0_6.Way(new CommonEntityData(id, info.getVersion(), getDate(info), getUser(info), info.getChangeset(), tags), nodes);
			} else {
				tmp = new org.openstreetmap.osmosis.core.domain.v0_6.Way(new CommonEntityData(id, NOVERSION, NODATE, OsmUser.NONE, NOCHANGESET, tags), nodes);

			}

			allWays.put(id, tmp);

		}
		System.out.println("Done.");
	}

	@Override
	protected void parse(final HeaderBlock header) {
		System.out.println("[INFO] Got header block.");
	}

	// Package-private methods

	/**
	 * Gets the osmosis object representing a the user in a given Info protobuf.
	 *
	 * @param info The info protobuf.
	 * @return The OsmUser object
	 */
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

}
