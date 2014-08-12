package com.inf.unnibz.algorithms.dijkstra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import com.inf.unibz.entity.Edge;
import com.inf.unibz.entity.Graph;
import com.inf.unibz.entity.Vertex;

public class DijkstraAlgorithm {

  private final List<Vertex> nodes;
  private final List<Edge> edges;
  private Set<Vertex> settledNodes;
  private Set<Vertex> unSettledNodes;
  private Map<Vertex, Vertex> predecessors;
  private Map<Vertex, Integer> distance;
  private PriorityQueue<Vertex> availableNodes;

  public DijkstraAlgorithm(Graph graph) {
    // create a copy of the array so that we can operate on this array
    this.nodes = new ArrayList<Vertex>(graph.getVertexes());
    this.edges = new ArrayList<Edge>(graph.getEdges());
    availableNodes = new PriorityQueue<Vertex>(0, new Comparator<Vertex>() {

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
    settledNodes = new HashSet<Vertex>();
    unSettledNodes = new HashSet<Vertex>();
    distance = new HashMap<Vertex, Integer>();
    predecessors = new HashMap<Vertex, Vertex>();
    distance.put(source, 0);
    distance.put(destination, Integer.MAX_VALUE);
//    unSettledNodes.add(source);
    availableNodes.add(source);
    availableNodes.add(destination);
    while (availableNodes.size() > 0 && getMinimum(unSettledNodes) != destination) {
//      Vertex node = getMinimum(unSettledNodes);
    	Vertex node = availableNodes.poll();
      settledNodes.add(node);
//      unSettledNodes.remove(node);
      findMinimalDistances(node);
    }
  }

 private void findMinimalDistances(Vertex node) {
//    List<Vertex> adjacentNodes = getNeighbors(node);
	  List<Vertex> adjacentNodes = node.getAdjacentNodes();
	  for (Vertex target : adjacentNodes) {
		  if(adjacentNodes.size() > 0)
		  	if (getShortestDistance(target) > getShortestDistance(node)
		          + getDistance(node, target)) {
		        distance.put(target, getShortestDistance(node)
		            + getDistance(node, target));
		        predecessors.put(target, node);
		//        unSettledNodes.add(target);
		        availableNodes.add(target);
		  }
	  }

  }

  private int getDistance(Vertex node, Vertex target) {
    for (Edge edge : edges) {
      if (edge.getSource().equals(node)
          && edge.getDestination().equals(target)) {
        return edge.getWeight();
      }
    }
    throw new RuntimeException("Should not happen");
  }

  public List<Vertex> getNeighbors(Vertex node) {
    List<Vertex> neighbors = new ArrayList<Vertex>();
    for (Edge edge : edges) {
      if (edge.getSource().equals(node)
          && !isSettled(edge.getDestination())) {
        neighbors.add(edge.getDestination());
      }
    }
    return neighbors;
  }

  private Vertex getMinimum(Set<Vertex> vertexes) {
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

  private int getShortestDistance(Vertex destination) {
    Integer d = distance.get(destination);
    if (d == null) {
      return Integer.MAX_VALUE;
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
  
  public void setGraphQueue(){
	  
  }

} 
