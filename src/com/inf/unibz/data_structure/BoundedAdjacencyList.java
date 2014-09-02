package com.inf.unibz.data_structure;

import com.inf.unibz.database.DBConnector;
import com.inf.unibz.entity.Edge;
import com.inf.unibz.entity.Vertex;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

public class BoundedAdjacencyList {
	
	private DBConnector db;
	private ArrayList<Vertex> verteces;
	private List<Edge> edges;
	private Set<Vertex> settledNodes;
	private LinkedList<Vertex> unsettledNodes;
	private List<Vertex> incompleteNodes;
	private Hashtable<String, Hashtable<Integer, ArrayList<Edge>>> subgraphs;
	
	public BoundedAdjacencyList(){
		db = new DBConnector("routing");
		verteces = db.getAllNodes();
		edges = db.getAllEdges();
		settledNodes = new HashSet<Vertex>();
		unsettledNodes = new LinkedList<Vertex>();
		subgraphs = new Hashtable<String, Hashtable<Integer, ArrayList<Edge>>>();
	}
	
	public void createGraph(){
		int i = 0;
		while(verteces.size() > 0)
			subgraphs.put("Subgraph " + i++, buildSubgraph(verteces.get(getSubgraphSource())));
	}
	
	public Hashtable<Integer, ArrayList<Edge>> buildSubgraph(Vertex source){
		Vertex v = source;
		verteces.remove(source);
		unsettledNodes.add(source);
		ArrayList<Edge> neighbors = null;
		Hashtable<Integer, ArrayList<Edge>> subgraph = new Hashtable<Integer, ArrayList<Edge>>();
		while(unsettledNodes.peek() != null){
			settledNodes.add(v);
			unsettledNodes.remove(v);
			neighbors = getNeighbors(v);
			
			subgraph.put(v.getId(), neighbors);
		}
		return subgraph;
	}
	
	public void addSubgraph(String key, Hashtable<Integer, ArrayList<Edge>> value){
		subgraphs.put(key, value);
	}
	
	public void addUnvisitedNode(ArrayList<Edge> list){
		for(Edge e : list)
			unsettledNodes.add(e.getDestination());
	}
	
	public boolean isSettled(Vertex v){
		if(settledNodes.contains(v))
			return true;
		return false;
	}
	
	public ArrayList<Edge> getNeighbors(Vertex node){
		ArrayList<Edge> neighbors = new ArrayList<Edge>();
	    	if(edges != null)
			    for (Edge edge : edges) {
			      if (edge.getSource().getId() == node.getId()){
			        neighbors.add(edge);
			        unsettledNodes.add(edge.getDestination());
			        verteces.remove(edge.getDestination());
			      }
			    }
	    return neighbors;
	}
	
	public List<Edge> completeUnsettledNodeEdge(){
		List<Edge> otherEdges = new ArrayList<Edge>();
		Edge e = null;
		for(Vertex v : unsettledNodes)
			for(Vertex vx : unsettledNodes)
				if(!v.equals(vx)){
					e = hasEdge(v, vx);
					if( e != null){
						otherEdges.add(e);
						settledNodes.add(e.getDestination());
					}
				}
		return otherEdges;
	}
	
	private Edge hasEdge(Vertex s, Vertex t){
		for(Edge e : edges)
			if((e.getSource().getId() == s.getId() && e.getDestination().getId() == t.getId()) || (e.getDestination().getId() == s.getId() && e.getSource().getId() == t.getId()))
				return e;
		return null;
	}
	
	public boolean removeNode(Vertex v){
		return verteces.remove(v);
	}
	
	private int getSubgraphSource(){
		Random rand = new Random(verteces.size()-1);
		return rand.nextInt();
	}
	

}
