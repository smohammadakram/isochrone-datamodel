package data_model.dijkstra;

public class TDEdge extends Edge {

	private final int eMode;
	private final int sMode;
	private final int dMode;
	private final int route;

	public TDEdge(final String id, final Vertex source, final Vertex destination, final double weight, final int eMode, final int sMode, final int dMode) {
		super(id, source, destination, weight);
		this.eMode = eMode;
		this.sMode = sMode;
		this.dMode = dMode;
		route = -1;
	}

	public TDEdge(final String id, final Vertex source, final Vertex destination, final double weight, final int eMode, final int sMode, final int dMode, final int route) {
		super(id, source, destination, weight);
		this.eMode = eMode;
		this.sMode = sMode;
		this.dMode = dMode;
		this.route = route;
	}

	public int geteMode() {
		return eMode;
	}

	public int getsMode() {
		return sMode;
	}

	public int getdMode() {
		return dMode;
	}

	public int getRoute() {
		return route;
	}

}
