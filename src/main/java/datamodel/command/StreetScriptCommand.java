package datamodel.command;


/**
 * This generates the script to add the street network for a city.
 */
public class StreetScriptCommand extends AbstractScriptCommand {

	public StreetScriptCommand(final String folder, final String city) {
		super(folder, city);
	}

	@Override
	public void execute() {
		System.out.println("[INFO] Output directory: " + getFolder());
		System.out.println("[INFO] City: " + getCity());

		CommandUtils.rethrowConsumer(this::createSqlFile).accept("street_network.sql");
	}

}
