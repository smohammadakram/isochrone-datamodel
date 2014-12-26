package time_expanded_spatial_data.street_network.components;

import java.util.Hashtable;

public class DenseNode {
	
	long id;
	DenseInfo dInfo;
	Hashtable<String, String> keyValuePairs;
	
	public DenseNode(long id, DenseInfo dInfo){
		this.id = id;
		this.dInfo = dInfo;
	}
	
	public DenseNode(long id, DenseInfo dInfo, Hashtable<String, String> keyValuePairs) {
		this.id = id;
		this.dInfo = dInfo;
		this.keyValuePairs = keyValuePairs;
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public DenseInfo getdInfo() {
		return dInfo;
	}
	
	public void setdInfo(DenseInfo dInfo) {
		this.dInfo = dInfo;
	}
	
	public Hashtable<String, String> getKeyValuePairs() {
		return keyValuePairs;
	}

	public void setKeyValuePairs(Hashtable<String, String> keyValuePairs) {
		this.keyValuePairs = keyValuePairs;
	}

}
