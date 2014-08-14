package com.inf.unibz.algorithms.dijkstra;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.inf.unibz.entity.Edge;
import com.inf.unibz.entity.Graph;
import com.inf.unibz.entity.Vertex;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TestDijkstraAlgorithm {

  private List<Vertex> nodes;
  private List<Edge> edges;

  @Test
  public void testExcute() {
    nodes = new ArrayList<Vertex>();
    edges = new ArrayList<Edge>();
    for (int i = 0; i < 11; i++) {
      Vertex location = new Vertex(i, "Node_" + i);
      nodes.add(location);
    }

    addLane(0, 0, 1, 85);
    addLane(1, 0, 2, 217);
    addLane(2, 0, 4, 173);
    addLane(3, 2, 6, 186);
    addLane(4, 2, 7, 103);
    addLane(5, 3, 7, 183);
    addLane(6, 5, 8, 250);
    addLane(7, 8, 9, 84);
    addLane(8, 7, 9, 167);
    addLane(9, 4, 9, 502);
    addLane(10, 9, 10, 40);
    addLane(11, 1, 10, 600);

    // Lets check from location Loc_1 to Loc_10
    Graph graph = new Graph(nodes, edges);
    DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
    dijkstra.execute(nodes.get(0), nodes.get(5));
    LinkedList<Vertex> path = dijkstra.getPath(nodes.get(10));
    
    assertNotNull(path);
    assertTrue(path.size() > 0);
    
    for (Vertex vertex : path) {
      System.out.println(vertex);
    }
    
  }

  private void addLane(int laneId, int sourceLocNo, int destLocNo,
      int duration) {
    Edge lane = new Edge(laneId,nodes.get(sourceLocNo), nodes.get(destLocNo), duration);
    edges.add(lane);
  }
} 