package data_model.time_expanded.bus_network;

public class Service {
	
	private int id;
	private String startDate;
	private String endDate;
	private boolean[] validity;
	
	public void setId(int id){
		this.id = id;
	}
	
	public int getId(){
		return id;
	}
	
	public void setStartDate(String sDate){
		startDate = sDate;
	}
	
	public String getStartDate(){
		return startDate;
	}
	
	public void setEndDate(String endDate){
		this.endDate = endDate;
	}
	
	public String getEndDate(){
		return endDate;
	}
	
	public void setValidity(boolean...validity){
		this.validity = validity;
	}
	
	public boolean[] getValidity(){
		return validity;
	}

}
