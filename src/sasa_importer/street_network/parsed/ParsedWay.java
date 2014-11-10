package sasa_importer.street_network.parsed;

public class ParsedWay {
	
	int version;
	String timestamp;
	int changeset;
	double latitude;
	double lognitude;
	
	public ParsedWay(int version, String timestamp, int changeset,
			double latitude, double lognitude) {
		super();
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
	
}
