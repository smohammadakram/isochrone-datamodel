package com.inf.unibz.entity;

import java.util.ArrayList;

public class Trip {
	
	private int id;
	private int route;
	private ArrayList<BusStop> tripSequence;

	public Trip(int route, int id){
		this.id = id;
		this.route = route;
	}
	
	public int getRoute(){
		return route;
	}
	
	public int getId(){
		return id;
	}
}
