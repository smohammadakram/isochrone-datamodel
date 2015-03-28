package datamodel.command;

import datamodel.timedependent.database.TimeDepTablesDescription;
import datamodel.timeexpanded.database.ScriptGenerator;
import datamodel.timeexpanded.database.Table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenerateTableDep implements ICommand {
	private final String city;
	private final String folder;

	// Constructor

	public GenerateTableDep(final String folder, final String city) {
		super();
		this.city = city;
		this.folder = folder;
	}

	// Public methods

	@Override
	public void execute() {
		final ScriptGenerator sg = new ScriptGenerator(getAllTables(), folder);
		sg.writeScipt();
		sg.closeWriter();
	}

	// Package private methods

	Collection<Table> getAllTables() {
		final List<Table> tables = new ArrayList<Table>(5);
		tables.add(getTableNodes());
		tables.add(getTableEdges());
		tables.add(getTableRoutes());
		tables.add(getTableTrips());
		tables.add(getTableCalendar());

		return tables;
	}

	// Private methods

	private Table getTableCalendar() {
		final List<String> primaryKeys = new ArrayList<String>();
		final List<String> foreignKeys = new ArrayList<String>();
		final Map<String, String> attributes = new HashMap<String, String>();

		attributes.put("calendar_service_id", TimeDepTablesDescription.Calendar.CALENDAR_SERVICE_ID);
		attributes.put("calendar_start_date", TimeDepTablesDescription.Calendar.CALENDAR_START_DATE);
		attributes.put("calendar_end_date", TimeDepTablesDescription.Calendar.CALENDAR_END_DATE);
		attributes.put("calendar_monday", TimeDepTablesDescription.Calendar.CALENDAR_DAY);
		attributes.put("calendar_tueday", TimeDepTablesDescription.Calendar.CALENDAR_DAY);
		attributes.put("calendar_wednesday", TimeDepTablesDescription.Calendar.CALENDAR_DAY);
		attributes.put("calendar_thursday", TimeDepTablesDescription.Calendar.CALENDAR_DAY);
		attributes.put("calendar_friday", TimeDepTablesDescription.Calendar.CALENDAR_DAY);
		attributes.put("calendar_saturday", TimeDepTablesDescription.Calendar.CALENDAR_DAY);
		attributes.put("calendar_sunday", TimeDepTablesDescription.Calendar.CALENDAR_DAY);

		final Table tableCalendar = new Table(city + "_calendar", primaryKeys, foreignKeys, attributes);
		tableCalendar.setSchemaName(TimeDepTablesDescription.SCHEMA_NAME);
		return tableCalendar;
	}

	private Table getTableEdges() {
		final List<String> primaryKeys = new ArrayList<String>();
		final List<String> foreignKeys = new ArrayList<String>();
		final Map<String, String> attributes = new HashMap<String, String>();

		primaryKeys.add("edge_id");
		foreignKeys.add("edge_source;time_dependent;" + city + "_nodes;node_id");
		foreignKeys.add("edge_destination;time_dependent;" + city + "_nodes;node_id");
		foreignKeys.add("edge_source_outdegree;time_dependent;" + city + "_nodes;node_outdegree");
		foreignKeys.add("edge_source_c_outdegree;time_dependent;" + city + "_nodes;node_c_outdegree");
		foreignKeys.add("edge_target_indegree;time_dependent;" + city + "_nodes;node_outdegree");
		foreignKeys.add("edge_target_c_indegree;time_dependent;" + city + "_nodes;node_c_indegree");
		attributes.put("edge_id", TimeDepTablesDescription.Edges.EDGE_ID);
		attributes.put("edge_source", TimeDepTablesDescription.Edges.EDGE_SOURCE);
		attributes.put("edge_destination", TimeDepTablesDescription.Edges.EDGE_DESTINATION);
		attributes.put("edge_length", TimeDepTablesDescription.Edges.EDGE_LENGTH);
		attributes.put("edge_route_id", TimeDepTablesDescription.Edges.EDGE_ROUTE_ID);
		attributes.put("edge_source_outdegree", TimeDepTablesDescription.Edges.EDGE_SOURCE_OUTDEGREE);
		attributes.put("edge_source_c_outdegree", TimeDepTablesDescription.Edges.EDGE_SOURCE_C_OUTDEGREE);
		attributes.put("edge_geometry", TimeDepTablesDescription.Edges.EDGE_GEOMETRY);
		attributes.put("edge_mode", TimeDepTablesDescription.Edges.EDGE_MODE);
		attributes.put("edge_source", TimeDepTablesDescription.Edges.EDGE_SOURCE_MODE);
		attributes.put("edge_destination", TimeDepTablesDescription.Edges.EDGE_DESTINATION_MODE);
		attributes.put("edge_source_x", TimeDepTablesDescription.Edges.EDGE_SOURCE_X);
		attributes.put("edge_source_y", TimeDepTablesDescription.Edges.EDGE_SOURCE_Y);
		attributes.put("edge_target_indegree", TimeDepTablesDescription.Edges.EDGE_TARGET_INDEGREE);
		attributes.put("edge_target_c_indegree", TimeDepTablesDescription.Edges.EDGE_TARGET_C_INDEGREE);

		final Table tableEdge = new Table(city + "_edges", primaryKeys, foreignKeys, attributes);
		tableEdge.setSchemaName(TimeDepTablesDescription.SCHEMA_NAME);
		return tableEdge;
	}

	private Table getTableNodes() {
		final List<String> primaryKeys = new ArrayList<String>();
		final List<String> foreignKeys = new ArrayList<String>();
		final Map<String, String> attributes = new HashMap<String, String>();

		// Creating Table object for nodes.
		primaryKeys.add("node_id");
		attributes.put("node_id", TimeDepTablesDescription.Nodes.NODE_ID);
		attributes.put("node_type", TimeDepTablesDescription.Nodes.NODE_TYPE);
		attributes.put("node_geometry", TimeDepTablesDescription.Nodes.NODE_GEOMETRY);
		attributes.put("node_mode", TimeDepTablesDescription.Nodes.NODE_MODE);
		attributes.put("node_target_indegree", TimeDepTablesDescription.Nodes.NODE_TARGET_INDEGREE);
		attributes.put("node_target_c_indegree", TimeDepTablesDescription.Nodes.NODE_TARGET_INDEGREE);
		attributes.put("node_indegree", TimeDepTablesDescription.Nodes.NODE_INDEGREE);
		attributes.put("node_c_indegree", TimeDepTablesDescription.Nodes.NODE_C_INDEGREE);
		attributes.put("node_outdegree", TimeDepTablesDescription.Nodes.NODE_OUTDEGREE);
		attributes.put("node_c_outdegree", TimeDepTablesDescription.Nodes.NODE_C_OUTDEGREE);
		attributes.put("node_target_indegree", TimeDepTablesDescription.Nodes.NODE_TARGET_INDEGREE);

		final Table tableNodes = new Table(city + "_nodes", primaryKeys, foreignKeys, attributes);
		tableNodes.setSchemaName(TimeDepTablesDescription.SCHEMA_NAME);
		return tableNodes;
	}

	private Table getTableRoutes() {
		final List<String> primaryKeys = new ArrayList<String>();
		final List<String> foreignKeys = new ArrayList<String>();
		final Map<String, String> attributes = new HashMap<String, String>();

		primaryKeys.add("route_id");
		attributes.put("route_id", TimeDepTablesDescription.Routes.ROUTE_ID);
		attributes.put("route_short_name", TimeDepTablesDescription.Routes.ROUTE_SHORT_NAME);
		attributes.put("route_long_name", TimeDepTablesDescription.Routes.ROUTE_LONG_NAME);
		attributes.put("route_desc", TimeDepTablesDescription.Routes.ROUTE_DESC);
		attributes.put("route_type", TimeDepTablesDescription.Routes.ROUTE_TYPE);
		attributes.put("route_agency_id", TimeDepTablesDescription.Routes.ROUTE_AGENCY_ID);

		final Table tableRoutes = new Table(city + "_routes", primaryKeys, foreignKeys, attributes);
		tableRoutes.setSchemaName(TimeDepTablesDescription.SCHEMA_NAME);
		return tableRoutes;
	}

	private Table getTableTrips() {
		final List<String> primaryKeys = new ArrayList<String>();
		final List<String> foreignKeys = new ArrayList<String>();
		final Map<String, String> attributes = new HashMap<String, String>();

		primaryKeys.add("trip_id");
		primaryKeys.add("trip_route_id");
		primaryKeys.add("trip_source");
		primaryKeys.add("trip_time_a");
		primaryKeys.add("trip_destination");
		primaryKeys.add("trip_time_a");
		attributes.put("trip_id", TimeDepTablesDescription.Trips.TRIP_ID);
		attributes.put("trip_id", TimeDepTablesDescription.Trips.TRIP_ID);
		attributes.put("trip_source", TimeDepTablesDescription.Trips.TRIP_SOURCE);
		attributes.put("trip_time_d", TimeDepTablesDescription.Trips.TRIP_TIME_D);
		attributes.put("trip_destination", TimeDepTablesDescription.Trips.TRIP_DESTINATION);
		attributes.put("trip_time_a", TimeDepTablesDescription.Trips.TRIP_TIME_A);
		attributes.put("trip_service_id", TimeDepTablesDescription.Trips.TRIP_SERVICE_ID);
		attributes.put("trip_edge_id", TimeDepTablesDescription.Trips.TRIP_EDGE_ID);
		attributes.put("trip_source_geo", TimeDepTablesDescription.Trips.TRIP_SOURCE_GEO);

		final Table tableTrips = new Table(city + "_trips", primaryKeys, foreignKeys, attributes);
		tableTrips.setSchemaName(TimeDepTablesDescription.SCHEMA_NAME);
		return tableTrips;
	}

}
