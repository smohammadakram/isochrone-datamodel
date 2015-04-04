package datamodel.command;

import datamodel.impl.ScriptGenerator;

import java.io.File;

/**
 * This parses the data from OpenStreetMap.
 * It inserts nodes and edges into the database.
 */
public class LinkCommand implements ICommand {
	private final String city;
	private final String folder;
	private final String scriptPrefix;

	public LinkCommand(final String folder, final String city) {
		super();
		this.city = city;
		this.folder = folder;

		scriptPrefix = folder + File.separatorChar + city + "_";
	}

	@Override
	public void execute() {
		final ICommand scriptCommand = new LinkScriptCommand(folder, city);
		scriptCommand.execute();

		ScriptGenerator.executeScript(scriptPrefix + "link_network.sql", city);

		final ICommand netCommand = new LinkNetCommand(city);
		netCommand.execute();
	}

}
