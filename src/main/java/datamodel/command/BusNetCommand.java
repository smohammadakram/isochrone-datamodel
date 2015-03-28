package datamodel.command;

import datamodel.database.ScriptGenerator;
import datamodel.timeexpanded.busnetwork.BusSQL;

import java.io.IOException;

public class BusNetCommand implements ICommand {
	private final String city;
	private final String folder;

	public BusNetCommand(final String folder, final String city) {
		super();
		this.city = city;
		this.folder = folder;
	}

	@Override
	public void execute() throws IOException {
		System.out.println("[INFO] Output directory: " + folder);
		System.out.println("[INFO] City: " + city);

		final ScriptGenerator sg0 = new ScriptGenerator(BusSQL.BUS_NODES_EDGES, "<city>", city);
		sg0.writeScript(folder + "/" + city + "_create_bus_nodes_edges.sql");

		final ScriptGenerator sg1 = new ScriptGenerator(BusSQL.BUS_TRIPS_IMPORT, "<city>", city);
		sg1.writeScript(folder + "/" + city + "_bus_trips_import.sql");

		final ScriptGenerator sg2 = new ScriptGenerator(BusSQL.BUILD_BUS_NETWORK, "<city>", city);
		sg2.writeScript(folder + "/" + city + "_bus_network.sql");
	}

}
