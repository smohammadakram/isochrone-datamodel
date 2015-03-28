package data_model.time_expanded.street_network.components;

import java.util.ArrayList;
import java.util.List;

public class OSMWay {

	private long changeset;
	private long id;
	private long timestamp;
	private int version;
	private List<Long> wayNodes;

	public OSMWay(final int version, final long id, final long timestamp, final int changeset, final Long... wayNodes) {
		this.version = version;
		this.id = id;
		this.timestamp = timestamp;
		this.changeset = changeset;
		for (final Long l : wayNodes) {
			this.wayNodes.add(l);
		}
	}

	public OSMWay(final int version, final long id, final long timestamp, final long changeset, final List<Long> wayNodes) {
		this.version = version;
		this.id = id;
		this.timestamp = timestamp;
		this.changeset = changeset;
		this.wayNodes = wayNodes;
	}

	public long getChangeset() {
		return changeset;
	}

	public long getId() {
		return id;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public int getVersion() {
		return version;
	}

	public List<Long> getWayNodes() {
		return wayNodes;
	}

	public void setChangeset(final long changeset) {
		this.changeset = changeset;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public void setTimestamp(final long timestamp) {
		this.timestamp = timestamp;
	}

	public void setVersion(final int version) {
		this.version = version;
	}

	public void setWayNodes(final ArrayList<Long> wayNodes) {
		this.wayNodes = wayNodes;
	}

	public void setWaysNodes(final Long... longs) {
		for (final Long l : longs) {
			wayNodes.add(l);
		}
	}

}
