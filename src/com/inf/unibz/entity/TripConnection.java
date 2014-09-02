package com.inf.unibz.entity;

public class TripConnection {
	
	private int tripID;
	private int tripSeqNr;
	private int lineVariant;
	private String depTime;
	private String arrTime;
	private int edgeID;
	private int sourceID;
	private int targetID;
	
	public void setTripID(int id){
		tripID = id;
	}
	
	public int getTripID(){
		return tripID;
	}
	
	public void setTripSeqNr(int seqNr){
		tripSeqNr = seqNr;
	}
	
	public int getTripSeqNr(){
		return tripSeqNr;
	}
	
	public void setLineVariant(int variant){
		lineVariant = variant;
	}
	
	public int getLineVariant(){
		return lineVariant;
	}
	
	public void setDepTime(String depTime){
		this.depTime = depTime;
	}
	
	public String getDepTime(){
		return depTime;
	}
	
	public void setArrTime(String arrTime){
		this.arrTime = arrTime;
	}
	
	public String getArrTime(){
		return arrTime;
	}
	
	public void setEdgeID(int id){
		edgeID = id;
	}
	
	public int getEdgeID(){
		return edgeID;
	}
	
	public void setSourceID(int id){
		sourceID = id;
	}
	
	public int getSourceID(){
		return sourceID;
	}
	
	public void setTargetID(int id){
		targetID = id;
	}
	
	public int getTargetID(){
		return targetID;
	}

}
