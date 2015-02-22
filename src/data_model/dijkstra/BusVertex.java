package data_model.dijkstra;

public class BusVertex extends Vertex {

	private final int route;
	
	public BusVertex(String id, String name, int route) {
		super(id, name);
		this.route = route;
	}

	public int getRoute() {
		return route;
	}
	
}
