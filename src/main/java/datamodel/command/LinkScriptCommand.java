package datamodel.command;

import datamodel.timeexpanded.database.ScriptGenerator;
import datamodel.timeexpanded.database.TimeExpTablesDescription;
import datamodel.timeexpanded.linknetwork.LinkSQL;

public class LinkScriptCommand implements ICommand {
	private final String city;
	private final String folder;

	public LinkScriptCommand(final String folder, final String city) {
		super();
		this.city = city;
		this.folder = folder;
	}

	@Override
	public void execute() {
		final String filePrefix = folder + "/" + city;
		final String tablePrefix = TimeExpTablesDescription.SCHEMA_NAME + "." + city;

		final ScriptGenerator sg0 = new ScriptGenerator(filePrefix + "_link_network.sql");
		sg0.createByReplace(LinkSQL.LINK_NETWORK, "<city>", city);
		sg0.writeScipt();
		sg0.closeWriter();

		final ScriptGenerator sg1 = new ScriptGenerator(filePrefix + "_update_street_nodes_degrees.sql");
		sg1.writeScipt("UPDATE " + tablePrefix + "_street_nodes AS s SET node_in_degree = tmp.in_d\n"
			+ "FROM (SELECT sn.node_id, count(edge_destination) AS in_d\n" + "\tFROM " + tablePrefix
			+ "_street_nodes sn JOIN " + tablePrefix + "_street_edges se\n" + "\t\tON sn.node_id = se.edge_destination\n" + "\tGROUP BY sn.node_id, se.edge_destination\n"
			+ "\tORDER BY sn.node_id) AS tmp\n" + "WHERE s.node_id = tmp.node_id;\n\n" + "UPDATE  " + tablePrefix
			+ "_street_nodes AS s SET node_out_degree = tmp.out_d\n" + "FROM (SELECT sn.node_id, count(edge_source) AS out_d\n" + "FROM " + tablePrefix
			+ "_street_nodes sn JOIN " + tablePrefix + "final _street_edges se\n" + "\t\tON sn.node_id = se.edge_source\n" + "\tGROUP BY sn.node_id, se.edge_source\n"
			+ "\tORDER BY sn.node_id) as tmp\n" + "WHERE s.node_id = tmp.node_id;");
		sg1.closeWriter();

		final ScriptGenerator sg2 = new ScriptGenerator(filePrefix + "_update_street_nodes_degrees.sql");
		sg2.writeScipt();
		sg2.closeWriter();
	}

}
