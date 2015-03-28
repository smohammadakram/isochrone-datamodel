package datamodel.command;

import datamodel.database.ScriptGenerator;
import datamodel.timeexpanded.linknetwork.LinkSQL;

import java.io.IOException;

public class LinkScriptCommand implements ICommand {
	private final String city;
	private final String folder;

	public LinkScriptCommand(final String folder, final String city) {
		super();
		this.city = city;
		this.folder = folder;
	}

	@Override
	public void execute() throws IOException {
		final String filePrefix = folder + "/" + city;

		final ScriptGenerator sg0 = new ScriptGenerator(LinkSQL.LINK_NETWORK, "<city>", city);
		sg0.writeScript(filePrefix + "_link_network.sql");

		final ScriptGenerator sg1 = new ScriptGenerator(LinkSQL.UPDATE_NODE_DEGRESS, "<city>", city);
		sg1.writeScript(filePrefix + "_update_street_nodes_degrees.sql");
	}

}
