package datamodel.command;

import datamodel.timeexpanded.busnetwork.BusSQL;
import datamodel.timeexpanded.database.ScriptGenerator;

public class BusNetCommand implements ICommand {
	private final String city;
	private final String folder;

	public BusNetCommand(final String folder, final String city) {
		super();
		this.city = city;
		this.folder = folder;
	}

	@Override
	public void execute() {
		System.out.println("[INFO] Output directory: " + folder);
		System.out.println("[INFO] City: " + city);

		final ScriptGenerator sg0 = new ScriptGenerator(folder + "/" + city + "_create_bus_nodes_edges.sql");
		sg0.createBusScript(city, BusSQL.BUS_NODES_EDGES);
		sg0.writeScipt();
		sg0.closeWriter();

		final ScriptGenerator sg1 = new ScriptGenerator(folder + "/" + city + "_bus_trips_import.sql");
		sg1.createBusScript(city, BusSQL.BUS_TRIPS_IMPORT);
		sg1.writeScipt();
		sg1.closeWriter();

		final ScriptGenerator sg2 = new ScriptGenerator(folder + "/" + city + "_bus_network.sql");
		sg2.createByReplace(BusSQL.BUILD_BUS_NETWORK, "<city>", city);
		sg2.writeScipt();
		sg2.closeWriter();
	}

}
