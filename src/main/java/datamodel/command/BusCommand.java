package datamodel.command;

import datamodel.impl.ScriptGenerator;

import java.io.File;

/**
 * This parses the data from OpenStreetMap.
 * It inserts nodes and edges into the database.
 */
public class BusCommand implements ICommand {
	private final String city;
	private final String folder;
	private final String gtfs;
	private final String resourcePrefix;
	private final String scriptPrefix;

	public BusCommand(final String folder, final String gtfs, final String city) {
		super();
		this.city = city;
		this.folder = folder;
		this.gtfs = gtfs;

		resourcePrefix = "datamodel/db" + File.separatorChar;
		scriptPrefix = folder + File.separatorChar + city + "_";
	}

	@Override
	public void execute() {
		final ICommand scriptCommand = new BusScriptCommand(folder, city);
		scriptCommand.execute();

		ScriptGenerator.executeScript(scriptPrefix + "bus_network.sql", city);
		ScriptGenerator.executeScript(resourcePrefix + "tmp-create.sql", city);

		final ICommand netCommand = new BusNetCommand(gtfs, city);
		netCommand.execute();

		ScriptGenerator.executeScript(resourcePrefix + "tmp-views.sql", city);
		ScriptGenerator.executeScript(scriptPrefix + "bus_nodes_edges.sql", city);
		ScriptGenerator.executeScript(scriptPrefix + "bus_trip_schedule.sql", city);
		ScriptGenerator.executeScript(resourcePrefix + "tmp-drop.sql", city);
	}

}
