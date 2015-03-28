package datamodel.timeexpanded.streetnetwork.components;

public class Relation {

	private int id;
	private String timestamp;
	private int uID;
	private int version;

	public Relation(final int id, final int version, final String timestamp, final int uID) {
		super();
		this.id = id;
		this.version = version;
		this.timestamp = timestamp;
		this.uID = uID;
	}

	public int getId() {
		return id;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public int getuID() {
		return uID;
	}

	public int getVersion() {
		return version;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public void setTimestamp(final String timestamp) {
		this.timestamp = timestamp;
	}

	public void setuID(final int uID) {
		this.uID = uID;
	}

	public void setVersion(final int version) {
		this.version = version;
	}

}
