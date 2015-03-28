package datamodel.dijkstra;

public class BusVertex extends Vertex {

	private final int route;

	public BusVertex(final String id, final String name, final int route) {
		super(id, name);
		this.route = route;
	}

	public int getRoute() {
		return route;
	}

}
