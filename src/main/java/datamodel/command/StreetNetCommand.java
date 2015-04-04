package datamodel.command;

import datamodel.impl.street.StreetNetwork;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This parses the data from OpenStreetMap.
 * It creates .sql files with which one is able to write nodes and edges into the database.
 */
public class StreetNetCommand implements ICommand {
	private static final Logger LOGGER = LogManager.getLogger(StreetNetCommand.class);

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
		final StreetNetwork gb = new StreetNetwork(city, folder);
		CommandUtils.rethrowConsumer(gb::parsePBF).accept(file);

		LOGGER.info("Graph: " + gb.getNrNodes() + " nodes, " + gb.getNrEdges() + " edges");
	}

}
