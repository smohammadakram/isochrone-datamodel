package time_expanded_spatial_data.street_network.components;

import org.postgis.PGgeometry;

public class RealNode {
	
	long id;
	PGgeometry geometry;
	
	public RealNode(long id, PGgeometry geometry) {
		super();
		this.id = id;
		this.geometry = geometry;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public PGgeometry getGeometry() {
		return geometry;
	}

	public void setGeometry(PGgeometry geometry) {
		this.geometry = geometry;
	}
	
	public String toString(){
		return "Node: " + id + ", " + geometry.toString(); 
	}
	
}
