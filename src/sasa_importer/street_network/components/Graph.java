package sasa_importer.street_network.components;

import java.util.HashMap;
import java.util.List;

import sasa_importer.street_network.GraphBuilder;

public class Graph {
	
	private HashMap<Long, RealNode> nodes;
	private HashMap<Long, Edge> edges;
	
	public void buildGraph(GraphBuilder gb){
		buildNodes(gb);
	}
	
	public void buildNodes(GraphBuilder gb){
		List<RealNode> nodes = gb.getRealStreetNodes();
		for(RealNode rn : nodes)
			this.nodes.put(rn.getId(), rn);
	}
	
	public void buildEdges(GraphBuilder gb,  HashMap<Long, OSMWay> allWays){
		//TODO 
		edges = new HashMap<Long, Edge>();
	}

}
