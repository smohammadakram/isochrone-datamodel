package datamodel.linknetwork;

public class LinkEdge {

	private long destination;
	private int destinationMode;
	private long source;
	private int sourceMode;

	// Constructor

	public LinkEdge(final long source, final int sourceMode, final long destination, final int destinationMode) {
		super();
		this.source = source;
		this.sourceMode = sourceMode;
		this.destination = destination;
		this.destinationMode = destinationMode;
	}

	// Getters

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

}
