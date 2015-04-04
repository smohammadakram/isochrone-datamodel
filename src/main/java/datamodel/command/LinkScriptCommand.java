package datamodel.command;

public class LinkScriptCommand extends AbstractScriptCommand {
	private static final String FOLDER_RESOURCE = "datamodel/impl/link/";

	public LinkScriptCommand(final String folder, final String city) {
		super(folder, city);
	}

	@Override
	public void execute() {
		CommandUtils.rethrowConsumer(this::createSqlFile).accept(FOLDER_RESOURCE + "link_network.sql");
	}

}
