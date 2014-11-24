package sasa_importer.street_network.components;

import java.util.Hashtable;

public class Node {

	int id;
	Hashtable<String, String> keyValuePairs;
	long latitude;
	long longitude;
	
	public Node(int id, Hashtable<String, String> keyValuePairs, long latitude,
			long longitude) {
		super();
		this.id = id;
		this.keyValuePairs = keyValuePairs;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Hashtable<String, String> getKeyValuePairs() {
		return keyValuePairs;
	}

	public void setKeyValuePairs(Hashtable<String, String> keyValuePairs) {
		this.keyValuePairs = keyValuePairs;
	}

	public long getLatitude() {
		return latitude;
	}

	public void setLatitude(long latitude) {
		this.latitude = latitude;
	}

	public long getLongitude() {
		return longitude;
	}

	public void setLongitude(long longitude) {
		this.longitude = longitude;
	}
	
}
