package data_model.time_expanded.link_netowrk;

public class LinkEdge {

	private long destination;
	private int destinationMode;
	private long source;
	private int sourceMode;

	public LinkEdge(final long source, final int sourceMode, final long destination, final int destinationMode) {
		super();
		this.source = source;
		this.sourceMode = sourceMode;
		this.destination = destination;
		this.destinationMode = destinationMode;
	}

	public long getDestination() {
		return destination;
	}

	public int getDestinationMode() {
		return destinationMode;
	}

	public long getSource() {
		return source;
	}

	public int getSourceMode() {
		return sourceMode;
	}

	public void setDestination(final long destination) {
		this.destination = destination;
	}

	public void setDestinationMode(final int destinationMode) {
		this.destinationMode = destinationMode;
	}

	public void setSource(final long source) {
		this.source = source;
	}

	public void setSourceMode(final int sourceMode) {
		this.sourceMode = sourceMode;
	}

}
