package data_model.time_expanded.link_netowrk;

public class LinkEdge {
	
	private long source;
	private int sourceMode;
	private long destination;
	private int destinationMode;
	
	public LinkEdge(long source, int sourceMode, long destination,
			int destinationMode) {
		super();
		this.source = source;
		this.sourceMode = sourceMode;
		this.destination = destination;
		this.destinationMode = destinationMode;
	}

	public long getSource() {
		return source;
	}

	public void setSource(long source) {
		this.source = source;
	}

	public int getSourceMode() {
		return sourceMode;
	}

	public void setSourceMode(int sourceMode) {
		this.sourceMode = sourceMode;
	}

	public long getDestination() {
		return destination;
	}

	public void setDestination(long destination) {
		this.destination = destination;
	}

	public int getDestinationMode() {
		return destinationMode;
	}

	public void setDestinationMode(int destinationMode) {
		this.destinationMode = destinationMode;
	}
		
}
