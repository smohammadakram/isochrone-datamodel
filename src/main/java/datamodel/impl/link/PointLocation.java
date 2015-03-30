package datamodel.impl.link;

class PointLocation {

	private final String edgeGeom;
	private final float location;
	private final String nodeGeom;

	// Constructor

	public PointLocation(final String nodeGeom, final float location, final String edgeGeom) {
		super();
		this.nodeGeom = nodeGeom;
		this.location = location;
		this.edgeGeom = edgeGeom;
	}

	// Getter

	public String getEdgeGeom() {
		return edgeGeom;
	}

	public float getLocation() {
		return location;
	}

	public String getNodeGeom() {
		return nodeGeom;
	}

}
