package sasa_importer.street_network.components;

import java.util.ArrayList;

public class Way {
	
	int version;
	String timestamp;
	int changeset;
	double latitude;
	double lognitude;
	ArrayList<Long> wayNodes;

	public Way(int version, String timestamp, int changeset, double latitude, double lognitude, ArrayList<Long> wayNodes) {
		this.version = version;
		this.timestamp = timestamp;
		this.changeset = changeset;
		this.latitude = latitude;
		this.lognitude = lognitude;
		this.wayNodes = wayNodes;
	}
	
	public Way(int version, String timestamp, int changeset, double latitude, double lognitude, Long...wayNodes) {
		this.version = version;
		this.timestamp = timestamp;
		this.changeset = changeset;
		this.latitude = latitude;
		this.lognitude = lognitude;
		setWaysNodes(wayNodes);
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public int getChangeset() {
		return changeset;
	}

	public void setChangeset(int changeset) {
		this.changeset = changeset;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLognitude() {
		return lognitude;
	}

	public void setLognitude(double lognitude) {
		this.lognitude = lognitude;
	}

	public ArrayList<Long> getWayNodes() {
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
