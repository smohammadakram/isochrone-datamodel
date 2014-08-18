package com.inf.unibz.algorithms.dijkstra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import com.inf.unibz.data_structure.adjacency.AdjacencyList;
import com.inf.unibz.data_structure.adjacency.AdjacencyMatrix;
import com.inf.unibz.entity.Edge;
import com.inf.unibz.entity.Graph;
import com.inf.unibz.entity.Vertex;

public class DijkstraAlgorithm {
	
	public final static String MATRIX_WITH_TIME = "com.inf.unibz.MATRIX";
	public final static String MATRIX_NO_TIME = "com.inf.unibz.MATRIX";
	public final static String LIST = "com.inf.unibz.LIST";
	public final static String NO_STRUCTURE = "com.inf.unibz.NO_STRUCTURE";

	  private List<Vertex> nodes;
	  private List<Edge> edges;
	  private Set<Vertex> settledNodes;
	  private Map<Vertex, Vertex> predecessors;
	  private Map<Vertex, Double> distance;
	  private PriorityQueue<Vertex> availableNodes;
	  private int[] verteces;
	  private Hashtable<Integer, Integer> vertecesIndexes;
	  private Edge[][] matrixWithoutTime;
	  private Hashtable<Integer, ArrayList<Vertex>> listWithoutTime;
	
	  public DijkstraAlgorithm(Graph graph) {
	    // create a copy of the array so that we can operate on this array
	    this.nodes = new ArrayList<Vertex>(graph.getVertexes());
	    this.edges = new ArrayList<Edge>(graph.getEdges());
	    settledNodes = new HashSet<Vertex>();
	    availableNodes = new PriorityQueue<Vertex>(1, new Comparator<Vertex>() {
	    	
			@Override
			public int compare(Vertex o1, Vertex o2) {
				if(distance.get(o1) < distance.get(o2))
					return -1;
				else if(distance.get(o1) > distance.get(o2))
					return 1;
				return 0;
			}
	    });
	  }
	  
	  public DijkstraAlgorithm(AdjacencyMatrix matrix){
		  settledNodes = new HashSet<Vertex>();
		  matrixWithoutTime = matrix.getMatrixWithoutTime();
		  availableNodes = new PriorityQueue<Vertex>(1, new Comparator<Vertex>() {
				
			  @Override
			  public int compare(Vertex o1, Vertex o2) {
				  if(distance.get(o1) < distance.get(o2))
					  return -1;
				  else if(distance.get(o1) > distance.get(o2))
					  return 1;
				  return 0;
			  }
		  });
	  }
	  
	  public DijkstraAlgorithm(AdjacencyList list, List<Edge> edges){
		  settledNodes = new HashSet<Vertex>();
		  this.edges = edges;
		  listWithoutTime = list.getListWithoutTime();
		  availableNodes = new PriorityQueue<Vertex>(1, new Comparator<Vertex>() {
				
			  @Override
			  public int compare(Vertex o1, Vertex o2) {
				  if(distance.get(o1) < distance.get(o2))
					  return -1;
				  else if(distance.get(o1) > distance.get(o2))
					  return 1;
				  return 0;
			  }
		  });
	  }
	
	  public void execute(Vertex source, Vertex destination) {
		    distance = new HashMap<Vertex, Double>();
		    predecessors = new HashMap<Vertex, Vertex>();
		    distance.put(source, 0.0);
		    distance.put(destination, Double.MAX_VALUE);
			availableNodes.add(source);
			availableNodes.add(destination);
			Vertex node = source;
			boolean stop = false;
			while (availableNodes.size() > 0 && !stop) {
				if(availableNodes.peek() == destination){
					stop = true;
					predecessors.put(destination, node);
				}
				else{
					node = availableNodes.poll();
					settledNodes.add(node);
					findMinimalDistances(node);
				}
			}
	  }
	  
	  public void executeMatrix(Vertex source, Vertex destination){
		  distance = new HashMap<Vertex, Double>();
		  predecessors = new HashMap<Vertex, Vertex>();
		  distance.put(source, 0.0);
		  distance.put(destination, Double.MAX_VALUE);
		  availableNodes.add(source);
		  availableNodes.add(destination);
		  Vertex node = source;
		  boolean stop = false;
		  while (availableNodes.size() > 0 && !stop) {
			  if(availableNodes.peek() == destination){
				  stop = true;
				  predecessors.put(destination, node);
			  }
			  else{
				  node = availableNodes.poll();
				  settledNodes.add(node);
				  findMinimalDistancesMatrix(node);
			  }
		  }
	  }
	  
	  public void executeList(Vertex source, Vertex destination){
		  distance = new HashMap<Vertex, Double>();
		  predecessors = new HashMap<Vertex, Vertex>();
		  distance.put(source, 0.0);
		  distance.put(destination, Double.MAX_VALUE);
		  availableNodes.add(source);
		  availableNodes.add(destination);
		  Vertex node = source;
		  boolean stop = false;
		  while (availableNodes.size() > 0 && !stop) {
			  if(availableNodes.peek() == destination){
				  stop = true;
				  predecessors.put(destination, node);
			  }
			  else{
				  node = availableNodes.poll();
				  settledNodes.add(node);
				  findMinimalDistancesList(node);
			  }
			  System.out.println("Node extracted last: " + node.toString()); 
		  }
	  }
	  
	  private void findMinimalDistances(Vertex node) {
		  List<Vertex> adjacentNodes = getNeighbors(node);
		  if(adjacentNodes.size() > 0){
			  for (Vertex target : adjacentNodes) {
				  if (getShortestDistance(target) > getShortestDistance(node) + getDistance(node, target)) {
					  distance.put(target, getShortestDistance(node) + getDistance(node, target));
					  predecessors.put(target, node);
					  availableNodes.add(target);
				  }
			  }
		  }
	  }
	  
	  private void findMinimalDistancesMatrix(Vertex node) {
		  List<Vertex> adjacentNodes = getNeighborsMatrix(node);
		  if(adjacentNodes.size() > 0){
			  for (Vertex target : adjacentNodes) {
				  if (getShortestDistance(target) > getShortestDistance(node) + getDistanceMatrix(node, target)) {
					  distance.put(target, getShortestDistance(node) + getDistanceMatrix(node, target));
					  predecessors.put(target, node);
					  availableNodes.add(target);
				  }
			  }
		  }
	  }
	  
	  private void findMinimalDistancesList(Vertex node) {
		  List<Vertex> adjacentNodes = getNeighborsList(node);
		  if(adjacentNodes.size() > 0){
			  for (Vertex target : adjacentNodes) {
				  if (getShortestDistance(target) > getShortestDistance(node) + getDistanceList(edges, node, target)) {
					  distance.put(target, getShortestDistance(node) + getDistanceList(edges, node, target));
					  predecessors.put(target, node);
					  availableNodes.add(target);
				  }
			  }
		  }
	  }	  	  
	
	  private double getDistance(Vertex node, Vertex target) {
		  for (Edge edge : edges) {
		      if (edge.getSource().getId() == node.getId() && edge.getDestination().getId() == target.getId()) {
		        return edge.getWeight();
		      }
		    }
		    throw new RuntimeException("Should not happen");
	  }
	  
	  public double getDistanceMatrix(Vertex node, Vertex target){
		  Edge e = matrixWithoutTime[vertecesIndexes.get(node.getId())][vertecesIndexes.get(target.getId())];
		  if(e != null)
			  return e.getWeight();
		  return -1;
	  }
	  
	  public double getDistanceList(List<Edge> edges, Vertex node, Vertex target){
		  for(Edge e : edges)
			  if(e.getSource().getId() == node.getId() && e.getDestination().getId() == target.getId())
				  return e.getWeight();
		  return -1;
	  }
	
	  private List<Vertex> getNeighbors(Vertex node) {
	    List<Vertex> neighbors = new ArrayList<Vertex>();
	    	if(edges != null)
			    for (Edge edge : edges) {
			      if (edge.getSource().getId() == node.getId() && !isSettled(edge.getDestination()))
			        neighbors.add(edge.getDestination());
			    }
	    return neighbors;
	  }
	  
	  private List<Vertex> getNeighborsMatrix(Vertex node) {
		    List<Vertex> neighbors = new ArrayList<Vertex>();
		    int nodeID = node.getId();
		    int id = vertecesIndexes.get(nodeID);
		    for(int i =  0; i < vertecesIndexes.size(); i++){
		    	Edge e = matrixWithoutTime[id][i];
		    	if(e != null)
		    		neighbors.add(e.getDestination());
		    }
		    return neighbors;
	  }
	  
	  private List<Vertex> getNeighborsList(Vertex node) {
		    return listWithoutTime.get(node.getId());
	  }
		
	
	  public Vertex getMinimum(Set<Vertex> vertexes) {
	    Vertex minimum = null;
	    for (Vertex vertex : vertexes) {
	      if (minimum == null) {
	        minimum = vertex;
	      } else {
	        if (getShortestDistance(vertex) < getShortestDistance(minimum)) {
	          minimum = vertex;
	        }
	      }
	    }
	    return minimum;
	  }
	
	  private boolean isSettled(Vertex vertex) {
	    return settledNodes.contains(vertex);
	  }
	
	  private double getShortestDistance(Vertex destination) {
	    Double d = distance.get(destination);
	    if (d == null) {
	      return Double.MAX_VALUE;
	    } else {
	      return d;
	    }
	  }
	
	  /*
	   * This method returns the path from the source to the selected target and
	   * NULL if no path exists
	   */
	  public LinkedList<Vertex> getPath(Vertex target) {
		  LinkedList<Vertex> path = new LinkedList<Vertex>();
		  Vertex step = target;
		  // check if a path exists
		  if (predecessors.get(step) == null) {
			  return null;
		  }
		  path.add(step);
		  while (predecessors.get(step) != null) {
			  step = predecessors.get(step);
			  path.add(step);
		  }
		  // Put it into the correct order
		  Collections.reverse(path);
		  return path;
	  }
	  
	  private List<Vertex> getNeighboursInMatrix(int id){
		  Vertex v = null;
		  ArrayList<Vertex> result = new ArrayList<Vertex>();
		  int vertexIndex = vertecesIndexes.get(id);
		  Edge e = null;
		  for(int i = 0; i < vertecesIndexes.size(); i++){
			  e = matrixWithoutTime[vertexIndex][i];
			  if(e != null)
				  result.add(new Vertex(e.getDestination().getId(), ""));
		  }
		  return result;
	  }
	  
	  public Hashtable<Integer, Integer> generateVertecesIndexes(){
			if(vertecesIndexes == null)
				vertecesIndexes = new Hashtable<Integer, Integer>();
			for(int i = 0; i < verteces.length; i++)
				vertecesIndexes.put(verteces[i], i);
			return vertecesIndexes;
		}

	  public void setVertecesIndexes(Hashtable<Integer, Integer> verIdx){
			vertecesIndexes = verIdx;
	  }
} 