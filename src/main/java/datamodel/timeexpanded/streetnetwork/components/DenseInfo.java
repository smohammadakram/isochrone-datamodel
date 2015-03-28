package datamodel.timeexpanded.streetnetwork.components;

public class DenseInfo {

	private long changeset;
	private double latitude;
	private double lognitude;
	private long timestamp;
	private int version;

	public DenseInfo(final int version, final long timestamp, final long changeset, final double latitude, final double lognitude) {
		this.version = version;
		this.timestamp = timestamp;
		this.changeset = changeset;
		this.latitude = latitude;
		this.lognitude = lognitude;
	}

	public long getChangeset() {
		return changeset;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLognitude() {
		return lognitude;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public int getVersion() {
		return version;
	}

	public void setChangeset(final long changeset) {
		this.changeset = changeset;
	}

	public void setLatitude(final double latitude) {
		this.latitude = latitude;
	}

	public void setLognitude(final double lognitude) {
		this.lognitude = lognitude;
	}

	public void setTimestamp(final long timestamp) {
		this.timestamp = timestamp;
	}

	public void setVersion(final int version) {
		this.version = version;
	}

}
