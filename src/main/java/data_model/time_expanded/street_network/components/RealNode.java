package data_model.time_expanded.street_network.components;

import org.postgis.PGgeometry;

public class RealNode {

	private PGgeometry geometry;
	private long id;

	public RealNode(final long id, final PGgeometry geometry) {
		super();
		this.id = id;
		this.geometry = geometry;
	}

	public PGgeometry getGeometry() {
		return geometry;
	}

	public long getId() {
		return id;
	}

	public void setGeometry(final PGgeometry geometry) {
		this.geometry = geometry;
	}

	public void setId(final long id) {
		this.id = id;
	}

	@Override
	public String toString(){
		return "Node: " + id + ", " + geometry.toString();
	}

}
