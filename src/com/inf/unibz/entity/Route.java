package com.inf.unibz.entity;

import java.util.ArrayList;

public class Route {
	
	private int id;
	private ArrayList<Trip> routeTrips;
	
	public Route(int id){
		this.id = id;
	}
	
	public int getId(){
		return id;
	}
}
