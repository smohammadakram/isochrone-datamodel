package datamodel.command;

import datamodel.streetnetwork.GraphBuilder;

/**
 * This parses the data from OpenStreetMap.
 * It inserts nodes and edges into the database.
 */
public class StreetNetCommand implements ICommand {
	private final String city;
	private final String file;
	private final String folder;

	public StreetNetCommand(final String folder, final String city, final String file) {
		super();
		this.city = city;
		this.file = file;
		this.folder = folder;
	}

	@Override
	public void execute() {
		System.out.println("[INFO] Source file: " + file);
		System.out.println("[INFO] City: " + city);

		final GraphBuilder gb = new GraphBuilder(city, folder);
		gb.parsePBF(file);

		System.out.println("[INFO] Graph: " + gb.getNrNodes() + " nodes, " + gb.getNrEdges() + " edges");
	}

}