package datamodel.command;

import datamodel.database.ScriptGenerator;
import datamodel.database.Table;
import datamodel.timeexpanded.database.TimeExpTablesDescription;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenerateTableExp implements ICommand {
	private final String city;
	private final String folder;

	// Constructor

	public GenerateTableExp(final String folder, final String city) {
		super();
		this.city = city;
		this.folder = folder;
	}

	// Public methods

	@Override
	public void execute() throws IOException {
		final ScriptGenerator sg = new ScriptGenerator(getAllTables());
		sg.writeScript(folder);
	}

	public Collection<Table> getAllTables() {
		final List<Table> tables = new ArrayList<Table>();
		tables.add(getTableNodes());
		tables.add(getTableEdges());

		return tables;
	}

	// Private methods

	private Table getTableEdges() {
		final List<String> primaryKeys = new ArrayList<String>();
		final List<String> foreignKeys = new ArrayList<String>();
		final Map<String, String> attributes = new HashMap<String, String>();

		primaryKeys.add("edge_id");
		foreignKeys.add("edge_source;time_expanded;" + city + "_street_nodes;node_id");
		foreignKeys.add("edge_destination;time_expanded;" + city + "_street_nodes;node_id");
		attributes.put("edge_id", TimeExpTablesDescription.StreetEdges.EDGE_ID);
		attributes.put("edge_source", TimeExpTablesDescription.StreetEdges.EDGE_SOURCE);
		attributes.put("edge_destination", TimeExpTablesDescription.StreetEdges.EDGE_DESTINATION);
		attributes.put("edge_geometry", TimeExpTablesDescription.StreetEdges.EDGE_GEOMETRY);

		return new Table(TimeExpTablesDescription.SCHEMA_NAME, city + "_street_edges", primaryKeys, foreignKeys, attributes);
	}

	private Table getTableNodes() {
		final List<String> primaryKeys = new ArrayList<String>();
		final List<String> foreignKeys = new ArrayList<String>();
		final Map<String, String> attributes = new HashMap<String, String>();

		primaryKeys.add("node_id");
		attributes.put("node_id", TimeExpTablesDescription.StreetNodes.NODE_ID);
		attributes.put("node_in_degree", TimeExpTablesDescription.StreetNodes.NODE_IN_DEGREE);
		attributes.put("node_out_degree", TimeExpTablesDescription.StreetNodes.NODE_OUT_DEGREE);
		attributes.put("node_geometry", TimeExpTablesDescription.StreetNodes.NODE_GEOMETRY);

		return new Table(TimeExpTablesDescription.SCHEMA_NAME, city + "_street_nodes", primaryKeys, foreignKeys, attributes);
	}

}
