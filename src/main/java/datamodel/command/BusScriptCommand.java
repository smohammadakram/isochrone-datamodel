package datamodel.command;

import java.util.Arrays;


public class BusScriptCommand extends AbstractScriptCommand {

	public BusScriptCommand(final String folder, final String city) {
		super(folder, city);
	}

	@Override
	public void execute() {
		System.out.println("[INFO] Output directory: " + getFolder());
		System.out.println("[INFO] City: " + getCity());

		final String[] filenames = new String[] {
			"bus_nodes_edges.sql",
			"bus_trip_schedule.sql",
			"bus_network.sql"
		};

		Arrays.stream(filenames).forEach(CommandUtils.rethrowConsumer(this::createSqlFile));
	}

}
