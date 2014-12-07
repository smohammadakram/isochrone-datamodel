package sasa_importer.street_network.components;

import org.postgis.PGgeometry;

public class Edge {
	
	long source;
	long destination;
	PGgeometry geometry;
	int length;
	
	public Edge(long source, long destination, PGgeometry geometry, int length) {
		super();
		this.source = source;
		this.destination = destination;
		this.geometry = geometry;
		this.length = length;
	}

	public long getSource() {
		return source;
	}

	public void setSource(long source) {
		this.source = source;
	}

	public long getDestination() {
		return destination;
	}

	public void setDestination(long destination) {
		this.destination = destination;
	}

	public PGgeometry getGeometry() {
		return geometry;
	}

	public void setGeometry(PGgeometry geometry) {
		this.geometry = geometry;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}
	
}
