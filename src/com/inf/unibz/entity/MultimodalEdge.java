package com.inf.unibz.entity;

public class MultimodalEdge extends Edge {
	
	private int type;
	
	public MultimodalEdge(int id, Vertex source, Vertex destination, double weight, int type) {
		super(id, source, destination, weight);
		this.type = type;
	}
	
	public void setType(int type){
		this.type = type;
	}
	
	public int getType(){
		return type;
	}

}
