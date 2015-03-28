package data_model.time_expanded.street_network;

import java.util.List;

import org.openstreetmap.osmosis.osmbinary.Osmformat;

public class PBFBlock {

	private String[] table;
	private List<Osmformat.Way> ways;

	public PBFBlock(final List<Osmformat.Way> ways, final String[] table) {
		super();
		this.ways = ways;
		this.table = table;
	}

	public String[] getTable() {
		return table;
	}

	public List<Osmformat.Way> getWays() {
		return ways;
	}

	public void setTable(final String[] table) {
		this.table = table;
	}

	public void setWays(final List<Osmformat.Way> ways) {
		this.ways = ways;
	}

}
