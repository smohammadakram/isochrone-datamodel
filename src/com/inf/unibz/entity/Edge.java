package com.inf.unibz.entity;

import java.util.List;

public class Edge  {
	  private final int id; 
	  private final Vertex source;
	  private final Vertex destination;
	  private final double weight; 
	  private int type;
	  
	  public Edge(int id, Vertex source, Vertex destination, double weight) {
	    this.id = id;
	    this.source = source;
	    this.destination = destination;
	    this.weight = weight;
	  }
	  
	  public int getId() {
	    return id;
	  }
	  public Vertex getDestination() {
	    return destination;
	  }

	  public Vertex getSource() {
	    return source;
	  }
	  public double getWeight() {
	    return weight;
	  }
	  
	  @Override
	  public String toString() {
	    return source + " " + destination;
	  }
	  
	  public void setSourceNeighbours(List<Vertex> n){
		  source.setAdjacentNodes(n);
	  }
	  
	  public void setType(int type){
			this.type = type;
		}
		
		public int getType(){
			return type;
		}
	  
	} 
