package data_model.dijkstra;

import java.util.List;

public class Graph {

	private final List<Edge> edges;
	private final List<Vertex> vertexes;

	public Graph(final List<Vertex> vertexes, final List<Edge> edges) {
		this.vertexes = vertexes;
		this.edges = edges;
	}

	public List<Edge> getEdges() {
		return edges;
	}

	public List<Vertex> getVertexes() {
		return vertexes;
	}

}
