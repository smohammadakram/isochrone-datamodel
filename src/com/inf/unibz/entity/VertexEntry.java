package com.inf.unibz.entity;

import java.util.ArrayList;

public class VertexEntry extends Vertex {
	
	private ArrayList<TripConnection> entry;

	public VertexEntry(int id, String name, ArrayList<TripConnection> entry) {
		super(id, name);
	}

	public void setEntry(ArrayList<TripConnection> entry){
		this.entry = entry;
	}
	
	public ArrayList<TripConnection> getEntry(){
		return entry;
	}
}
