package datamodel.command;

import java.util.Arrays;

public class LinkScriptCommand extends AbstractScriptCommand {

	public LinkScriptCommand(final String folder, final String city) {
		super(folder, city);
	}

	@Override
	public void execute() {
		final String[] filenames = new String[] {
			"link_network.sql",
			"link_nodes_degrees.sql"
		};

		Arrays.stream(filenames).forEach(CommandUtils.rethrowConsumer(this::createSqlFile));
	}

}
