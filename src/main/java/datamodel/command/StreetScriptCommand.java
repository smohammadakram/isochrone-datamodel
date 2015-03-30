package datamodel.command;

import java.io.IOException;

/**
 * This generates the script to add the street network for a city.
 */
public class StreetScriptCommand extends AbstractScriptCommand {

	public StreetScriptCommand(final String folder, final String city) {
		super(folder, city);
	}

	@Override
	public void execute() throws IOException {
		System.out.println("[INFO] Output directory: " + getFolder());
		System.out.println("[INFO] City: " + getCity());

		createSqlFiles("street_network.sql");
	}

}
