package sasa_importer.street_network.components;

import java.util.HashMap;
import java.util.List;

import sasa_importer.street_network.GraphBuilder;

public class Graph {
	
	private HashMap<Long, RealNode> nodes;
	private HashMap<Long, Edge> edges;
	private GraphBuilder builder;
	
	public Graph(GraphBuilder gb){
		builder = gb;
	}
	
	public void buildGraph(){
//		builder.findCross();
		nodes = builder.buildNodes();
		edges = builder.buildEdges();
		builder.insertNodes(nodes.values());
		builder.insertEdges(edges.values());
	}
	
	public RealNode getNodeByID(Long id){
		RealNode result = nodes.get(id);
		return result;
	}
	
	public Edge getEdgeByID(Long id){
		Edge result = edges.get(id);
		return result;
	}
	
	public void printGraph(){
		System.out.println("Graph: " + nodes.size() + " nodes, " + edges.size() + " edges");
	}

}
