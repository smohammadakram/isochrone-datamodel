package datamodel.command;

import java.util.Arrays;

public class BusScriptCommand extends AbstractScriptCommand {

	public BusScriptCommand(final String folder, final String city) {
		super(folder, city);
	}

	@Override
	public void execute() {
		final String[] filenames = new String[] {
			"bus_network.sql",
			"bus_nodes_edges.sql",
			"bus_trip_schedule.sql"
		};

		Arrays.stream(filenames).forEach(CommandUtils.rethrowConsumer(this::createSqlFile));
	}

}
