package datamodel.command;

import java.io.IOException;

public class LinkScriptCommand extends AbstractSqlCommand {

	public LinkScriptCommand(final String folder, final String city) {
		super(folder, city);
	}

	@Override
	public void execute() throws IOException {
		createSqlFiles(new String[] {
			"link_network.sql",
			"link_nodes_degrees.sql"
		});
	}

}
