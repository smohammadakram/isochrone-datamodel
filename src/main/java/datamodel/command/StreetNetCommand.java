package datamodel.command;

import datamodel.streetnetwork.GraphBuilder;
import datamodel.util.DBConnector;

import java.io.File;

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

		final DBConnector db = new DBConnector();
		final File f1 = new File(folder + "/" + city + "_street_nodes_import.sql");
		db.openWriter(f1.getAbsolutePath(), false);
		db.deleteClause(city + "_street_nodes");
		db.closeWriter();

		final File f2 = new File(folder + "/" + city + "_street_edges_import.sql");
		db.openWriter(f2.getAbsolutePath(), false);
		db.deleteClause(city + "_street_edges");
		db.closeWriter();

		final GraphBuilder gb = new GraphBuilder(file, folder, city, db);
		gb.parsePBF();

		System.out.println("[INFO] Graph: " + gb.getNrNodes() + " nodes, " + gb.getNrEdges() + " edges");
	}

}
