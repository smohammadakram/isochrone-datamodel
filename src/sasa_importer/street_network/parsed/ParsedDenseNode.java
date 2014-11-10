package sasa_importer.street_network.parsed;

public class ParsedDenseNode {
	
	int id;
	ParsedDenseInfo dInfo;
	
	public ParsedDenseNode(int id, ParsedDenseInfo dInfo) {
		this.id = id;
		this.dInfo = dInfo;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public ParsedDenseInfo getdInfo() {
		return dInfo;
	}
	public void setdInfo(ParsedDenseInfo dInfo) {
		this.dInfo = dInfo;
	}
	

}
