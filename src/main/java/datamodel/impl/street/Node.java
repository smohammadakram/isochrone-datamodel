package datamodel.impl.street;

import org.postgis.PGgeometry;

public class Node {

	private final PGgeometry geometry;
	private final long id;

	// Constructor

	public Node(final long id, final PGgeometry geometry) {
		super();
		this.id = id;
		this.geometry = geometry;
	}

	// Getter

	public PGgeometry getGeometry() {
		return geometry;
	}

	public long getId() {
		return id;
	}

	// Public methods

	@Override
	public String toString() {
		return "Node: " + id + ", " + geometry.toString();
	}

}
