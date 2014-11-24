package sasa_importer.street_network.components;

public class DenseInfo {
	
	int version;
	long timestamp;
	long changeset;
	long latitude;
	long lognitude;
	
	public DenseInfo(int version, long timestamp, long changeset, long latitude, long lognitude) {
		this.version = version;
		this.timestamp = timestamp;
		this.changeset = changeset;
		this.latitude = latitude;
		this.lognitude = lognitude;
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
	
	public long getLatitude() {
		return latitude;
	}
	
	public void setLatitude(long latitude) {
		this.latitude = latitude;
	}
	
	public long getLognitude() {
		return lognitude;
	}
	
	public void setLognitude(long lognitude) {
		this.lognitude = lognitude;
	}

}
