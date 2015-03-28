package data_model.time_expanded.street_network.components;

import java.util.Hashtable;

public class DenseNode {

	private DenseInfo dInfo;
	private long id;
	private Hashtable<String, String> keyValuePairs;

	public DenseNode(final long id, final DenseInfo dInfo){
		this.id = id;
		this.dInfo = dInfo;
	}

	public DenseNode(final long id, final DenseInfo dInfo, final Hashtable<String, String> keyValuePairs) {
		this.id = id;
		this.dInfo = dInfo;
		this.keyValuePairs = keyValuePairs;
	}

	public DenseInfo getdInfo() {
		return dInfo;
	}

	public long getId() {
		return id;
	}

	public Hashtable<String, String> getKeyValuePairs() {
		return keyValuePairs;
	}

	public void setdInfo(final DenseInfo dInfo) {
		this.dInfo = dInfo;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public void setKeyValuePairs(final Hashtable<String, String> keyValuePairs) {
		this.keyValuePairs = keyValuePairs;
	}

}
