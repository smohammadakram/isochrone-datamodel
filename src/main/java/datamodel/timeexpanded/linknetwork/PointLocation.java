package datamodel.timeexpanded.linknetwork;

public class PointLocation {

	private String edgeGeom;
	private float location;
	private String nodeGeom;

	public PointLocation(final String nodeGeom, final float location, final String edgeGeom) {
		super();
		this.nodeGeom = nodeGeom;
		this.location = location;
		this.edgeGeom = edgeGeom;
	}

	public String getEdgeGeom() {
		return edgeGeom;
	}

	public float getLocation() {
		return location;
	}

	public String getNodeGeom() {
		return nodeGeom;
	}

	public void setEdgeGeom(final String edgeGeom) {
		this.edgeGeom = edgeGeom;
	}

	public void setLocation(final float location) {
		this.location = location;
	}

	public void setNodeGeom(final String nodeGeom) {
		this.nodeGeom = nodeGeom;
	}

}
