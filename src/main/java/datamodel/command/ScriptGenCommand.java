package datamodel.command;

import datamodel.timeexpanded.database.ScriptGenerator;
import datamodel.timeexpanded.streetnetwork.StreetSQL;

/**
 * This generates the script to add the street network for a city.
 */
public class ScriptGenCommand implements ICommand {
	private final String city;
	private final String folder;

	public ScriptGenCommand(final String folder, final String city) {
		super();
		this.city = city;
		this.folder = folder;
	}

	@Override
	public void execute() {
		System.out.println("[INFO] Output directory: " + folder);
		System.out.println("[INFO] City: " + city);

		final ScriptGenerator sg = new ScriptGenerator(folder + "/" + city + "_street_network.sql");
		sg.createByReplace(StreetSQL.BUILD_STREET_NETWORK, "<city>", city);
		sg.writeScipt();
		sg.closeWriter();
	}

}
