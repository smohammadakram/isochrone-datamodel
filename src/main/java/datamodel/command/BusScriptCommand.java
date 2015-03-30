package datamodel.command;

import java.io.IOException;

public class BusScriptCommand extends AbstractScriptCommand {

	public BusScriptCommand(final String folder, final String city) {
		super(folder, city);
	}

	@Override
	public void execute() throws IOException {
		System.out.println("[INFO] Output directory: " + getFolder());
		System.out.println("[INFO] City: " + getCity());

		createSqlFiles(new String[] {
			"bus_nodes_edges.sql",
			"bus_trip_schedule.sql",
			"bus_network.sql"
		});
	}

}
