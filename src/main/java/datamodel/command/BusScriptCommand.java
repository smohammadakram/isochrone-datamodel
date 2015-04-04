package datamodel.command;

import java.util.Arrays;

public class BusScriptCommand extends AbstractScriptCommand {
	private static final String FOLDER_RESOURCE = "datamodel/impl/bus/";

	public BusScriptCommand(final String folder, final String city) {
		super(folder, city);
	}

	@Override
	public void execute() {
		final String[] filenames = new String[] {
			FOLDER_RESOURCE + "bus_network.sql",
			FOLDER_RESOURCE + "bus_nodes_edges.sql",
			FOLDER_RESOURCE + "bus_trip_schedule.sql"
		};

		Arrays.stream(filenames).forEach(CommandUtils.rethrowConsumer(this::createSqlFile));
	}

}
