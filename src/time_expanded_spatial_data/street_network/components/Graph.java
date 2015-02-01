package time_expanded_spatial_data.street_network.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import time_expanded_spatial_data.street_network.GraphBuilder;

public class Graph {
	
	private HashMap<Long, RealNode> nodes;
	private ArrayList<Edge> edges;
	private GraphBuilder builder;
	
	public Graph(GraphBuilder gb){
		builder = gb;
	}
	
	public void buildGraph(){
//		builder.parsePBF();
//		nodes = builder.buildNodes();
//		edges = builder.buildEdges();
	}
	
	public RealNode getNodeByID(Long id){
		RealNode result = nodes.get(id);
		return result;
	}
	
	public void printGraph(){
//		System.out.println("[INFO] Graph: " + nodes.size() + " nodes, " + edges.size() + " edges");
		System.out.println("[INFO] Graph: " + builder.nrNodes + " nodes, " + builder.nrEdges + " edges");
	}

}
