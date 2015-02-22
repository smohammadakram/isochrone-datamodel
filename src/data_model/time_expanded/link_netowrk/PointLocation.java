package data_model.time_expanded.link_netowrk;

public class PointLocation {
	
	private String nodeGeom;
	private float location;
	private String edgeGeom;
	
	public PointLocation(String nodeGeom, float location, String edgeGeom) {
		super();
		this.nodeGeom = nodeGeom;
		this.location = location;
		this.edgeGeom = edgeGeom;
	}

	public String getNodeGeom() {
		return nodeGeom;
	}

	public void setNodeGeom(String nodeGeom) {
		this.nodeGeom = nodeGeom;
	}

	public float getLocation() {
		return location;
	}

	public void setLocation(float location) {
		this.location = location;
	}

	public String getEdgeGeom() {
		return edgeGeom;
	}

	public void setEdgeGeom(String edgeGeom) {
		this.edgeGeom = edgeGeom;
	}
			
}
