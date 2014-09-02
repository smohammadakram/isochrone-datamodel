package com.inf.unibz.entity;

import java.util.ArrayList;

public class EdgeEntry extends Edge {
	
	private ArrayList<TripConnection> entry;
	
	public EdgeEntry(Edge e, ArrayList<TripConnection> entry){
		super(e.getId(), e.getSource(), e.getDestination(), e.getWeight());
		this.entry = entry;
	}

	public void setEntry(TripConnection...connections){
		for(TripConnection tc: connections)
			entry.add(tc);
	}
	
	public void setEntry(ArrayList<TripConnection> connections){
		entry = connections;
	}
	
	public ArrayList<TripConnection> getEntry(){
		return entry;
	}
} 
