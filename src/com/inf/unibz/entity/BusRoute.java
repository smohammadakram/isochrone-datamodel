package com.inf.unibz.entity;

public class BusRoute {
	
	private int routeID;
	private int stopID;
	
	public BusRoute(int stop, int route){
		routeID = route;
		stopID = stop;
	}
	
	public int getRoute(){
		return routeID;
	}
	
	public int getStop(){
		return stopID;
	}
	

}
