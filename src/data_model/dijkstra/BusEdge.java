package data_model.dijkstra;

public class BusEdge extends Edge {

	private final int route;

	public BusEdge(String id, Vertex source, Vertex destination, int weight, int route) {
		super(id, source, destination, weight);
		
		this.route = route;
	}

	public int getRoute() {
		return route;
	}

}
