package datamodel.timeexpanded.streetnetwork.components;

import org.postgis.PGgeometry;

public class Edge {

	private long destination;
	private PGgeometry geometry;
	private long id;
	private int length;
	private long source;

	public Edge(final long id, final long source, final long destination, final PGgeometry geometry, final int length) {
		super();
		this.id = id;
		this.source = source;
		this.destination = destination;
		this.geometry = geometry;
		this.length = length;
	}

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

	public void setDestination(final long destination) {
		this.destination = destination;
	}

	public void setGeometry(final PGgeometry geometry) {
		this.geometry = geometry;
	}

	public void setId(final long id){
		this.id = id;
	}

	public void setLength(final int length) {
		this.length = length;
	}

	public void setSource(final long source) {
		this.source = source;
	}

}
