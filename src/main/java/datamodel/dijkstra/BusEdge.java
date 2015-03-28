package datamodel.dijkstra;

public class BusEdge extends Edge {

	private final int route;

	public BusEdge(final String id, final Vertex source, final Vertex destination, final int weight, final int route) {
		super(id, source, destination, weight);

		this.route = route;
	}

	public int getRoute() {
		return route;
	}

}
