package datamodel.streetnetwork;

public class DenseNode {

	private final long changeset;
	private final double latitude;
	private final double lognitude;
	private final long timestamp;
	private final int version;

	// Constructor

	public DenseNode(final int version, final long timestamp, final long changeset, final double latitude, final double lognitude) {
		this.version = version;
		this.timestamp = timestamp;
		this.changeset = changeset;
		this.latitude = latitude;
		this.lognitude = lognitude;
	}

	// Getter

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

}
