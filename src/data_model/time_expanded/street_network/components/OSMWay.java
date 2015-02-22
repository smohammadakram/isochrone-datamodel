package data_model.time_expanded.street_network.components;

import java.util.ArrayList;
import java.util.List;

public class OSMWay {
	
	int version;
	long id;
	long timestamp;
	long changeset;
	List<Long> wayNodes;
	
	public OSMWay(int version, long id, long timestamp, long changeset, List<Long> wayNodes) {
		this.version = version;
		this.id = id;
		this.timestamp = timestamp;
		this.changeset = changeset;
		this.wayNodes = wayNodes;
	}
	
	public OSMWay(int version, long id, long timestamp, int changeset, Long...wayNodes) {
		this.version = version;
		this.id = id;
		this.timestamp = timestamp;
		this.changeset = changeset;
		for(Long l : wayNodes)
			this.wayNodes.add(l);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getChangeset() {
		return changeset;
	}

	public void setChangeset(long changeset) {
		this.changeset = changeset;
	}

	public List<Long> getWayNodes() {
		return wayNodes;
	}

	public void setWayNodes(ArrayList<Long> wayNodes) {
		this.wayNodes = wayNodes;
	}	
	
	public void setWaysNodes(Long...longs){
		for(Long l : longs)
			wayNodes.add(l);
	}
	
}
