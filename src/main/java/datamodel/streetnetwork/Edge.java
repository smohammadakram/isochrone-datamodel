package datamodel.streetnetwork;

import org.postgis.PGgeometry;

public class Edge {

	private final long destination;
	private final PGgeometry geometry;
	private final long id;
	private final int length;
	private final long source;

	// Constructor

	public Edge(final long id, final long source, final long destination, final PGgeometry geometry, final int length) {
		super();
		this.id = id;
		this.source = source;
		this.destination = destination;
		this.geometry = geometry;
		this.length = length;
	}

	// Getter

	public long getDestination() {
		return destination;
	}

	public PGgeometry getGeometry() {
		return geometry;
	}

	public long getId(){
		return id;
	}

	public int getLength() {
		return length;
	}

	public long getSource() {
		return source;
	}

}
