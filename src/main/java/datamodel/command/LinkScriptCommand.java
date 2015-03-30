package datamodel.command;

public class LinkScriptCommand extends AbstractScriptCommand {

	public LinkScriptCommand(final String folder, final String city) {
		super(folder, city);
	}

	@Override
	public void execute() {
		CommandUtils.rethrowConsumer(this::createSqlFile).accept("link_network.sql");
	}

}
