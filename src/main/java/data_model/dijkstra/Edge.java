package data_model.dijkstra;

public class Edge {

	private final Vertex destination;
	private final String id;
	private final Vertex source;
	private final double weight;

	public Edge(final String id, final Vertex source, final Vertex destination, final double weight) {
		this.id = id;
		this.source = source;
		this.destination = destination;
		this.weight = weight;
	}

	public Vertex getDestination() {
		return destination;
	}

	public String getId() {
		return id;
	}

	public Vertex getSource() {
		return source;
	}

	public double getWeight() {
		return weight;
	}

	@Override
	public String toString() {
		return source + " " + destination;
	}

}
