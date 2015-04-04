package datamodel.command;

import datamodel.impl.ScriptGenerator;

import java.io.File;

/**
 * This parses the data from OpenStreetMap.
 * It inserts nodes and edges into the database.
 */
public class StreetCommand implements ICommand {
	private final String city;
	private final String file;
	private final String folder;
	private final String scriptPrefix;

	public StreetCommand(final String folder, final String city, final String file) {
		super();
		this.city = city;
		this.file = file;
		this.folder = folder;

		scriptPrefix = folder + File.separatorChar + city + "_";
	}

	@Override
	public void execute() {
		final ICommand scriptCommand = new StreetScriptCommand(folder, city);
		scriptCommand.execute();

		ScriptGenerator.executeScript(scriptPrefix + "street_network.sql", city);

		final ICommand netCommand = new StreetNetCommand(folder, city, file);
		netCommand.execute();

		ScriptGenerator.executeScript(scriptPrefix + "street_nodes_import.sql", city);
		ScriptGenerator.executeScript(scriptPrefix + "street_edges_import.sql", city);
	}

}
