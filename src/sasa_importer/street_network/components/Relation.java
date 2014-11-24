package sasa_importer.street_network.components;

public class Relation {
	
	int id;
	int version;
	String timestamp;
	int uID;
	
	public Relation(int id, int version, String timestamp, int uID) {
		super();
		this.id = id;
		this.version = version;
		this.timestamp = timestamp;
		this.uID = uID;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public int getuID() {
		return uID;
	}

	public void setuID(int uID) {
		this.uID = uID;
	}

}
