package datamodel.command;

/**
 * This generates the script to add the street network for a city.
 */
public class StreetScriptCommand extends AbstractScriptCommand {
	private static final String FOLDER_RESOURCE = "datamodel/impl/street/";

	public StreetScriptCommand(final String folder, final String city) {
		super(folder, city);
	}

	@Override
	public void execute() {
		CommandUtils.rethrowConsumer(this::createSqlFile).accept(FOLDER_RESOURCE + "street_network.sql");
	}

}
