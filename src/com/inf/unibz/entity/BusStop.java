package com.inf.unibz.entity;

public class BusStop {
	
	private int id;
	private String name;
	private double longitude;
	private double latitude;
	
	public BusStop(int id, String name, double longitude, double latitude){
		this.id = id;
		this.name = name;
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
	public void setID(int id){
		this.id = id;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setLongitude(double longitude){
		this.longitude = longitude;
	}
	
	public void setLatitude(double latitude){
		this.latitude = latitude;
	}
	
	public int getID(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	public double getLongitude(){
		return longitude;
	}
	
	public double getLatitude(){
		return latitude;
	}
	
	public int getCoordinateAsInt(String coordinate){
		switch(coordinate){
		case "longitude":
			String longi = String.valueOf(this.longitude);
			System.out.println(longi);
			String s = null;
			if(longi.length() >= 9)
				s = longi.substring(0, 2) + longi.substring(3, 9);
			else
				s = longi.substring(0, 2) + longi.substring(3, longi.length()-1);
			System.out.println(s);
			return Integer.parseInt(s);
		case "latitude":
			String lat = String.valueOf(this.latitude);
			if(lat.length() >= 9)
				s = lat.substring(0, 2) + lat.substring(3, 9);
			else
				s = lat.substring(0, 2) + lat.substring(3, lat.length()-1);
			System.out.println(s);
			return Integer.parseInt(s);
		}
		return -1;
	}

}
