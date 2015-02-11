package time_expanded_spatial_data;

import java.io.File;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import time_expanded_spatial_data.bus_network.BusDataParser;
import time_expanded_spatial_data.bus_network.ViewText;
import time_expanded_spatial_data.database.DBConnector;
import time_expanded_spatial_data.database.ScriptGenerator;
import time_expanded_spatial_data.database.Table;
import time_expanded_spatial_data.database.TimeDepTablesDescription;
import time_expanded_spatial_data.database.TimeExpTablesDescription;
import time_expanded_spatial_data.link_netowrk.LinkNetwork;
import time_expanded_spatial_data.street_network.GraphBuilder;
import time_expanded_spatial_data.street_network.PBFParser;
import time_expanded_spatial_data.street_network.SQLText;
import time_expanded_spatial_data.street_network.components.Graph;

public class Main {
	
	public static void main(String[] args){
		System.gc();
		DBConnector db = null;
		switch (args[0]){
		
		/**
		 * Renaming the vdv file from *.x10 to *.X10.
		 */
		case "vdvnames": case "1":
			System.out.println("[INFO] File rename.");
			File vdv = new File(args[1]);
			File[] files = vdv.listFiles();
			for(File f : files){
				StringTokenizer st = new StringTokenizer(f.getName(), ".");
				String name = st.nextToken();
				f.renameTo(new File(args[1]+ "/" + name + ".x10"));
			}
			break;
			
		/**
		 * Populating temporary bus database.
		 */
		case "tmpdb": case "2":
			if(db == null)
				db = new DBConnector();
//			db.emptyTmpDatabase();
			final BusDataParser bdp = new BusDataParser(db, args[1], args[2]);
//			System.out.println("[INFO] Temporal database creation.");
			Thread t1 = new Thread(new Runnable() {
				
				@Override
				public void run() {
					bdp.parseRoutes();
				}
			});
			
			Thread t2 = new Thread(new Runnable() {
				
				@Override
				public void run() {
					bdp.parseTrips();
				}
			});
			
			Thread t3 = new Thread(new Runnable() {
				
				@Override
				public void run() {
					bdp.parseCalendar();
					bdp.createCalendar();
				}
			});
			
			Thread t4 = new Thread(new Runnable() {
				
				@Override
				public void run() {
					bdp.parseStops();
				}
			});
			
			Thread t5 = new Thread(new Runnable() {
				
				@Override
				public void run() {
					bdp.parseTripSequence();
				}
			});
			t1.start();
			t2.start();
			t3.start();
			t4.start();
			t5.start();
			break;
			
		/**
		 * Creating link network.
		 */
		case "linknet": case "3":
			db = new DBConnector();
			final LinkNetwork ln = new LinkNetwork(db, args[1]);
			Thread t6 = new Thread(new Runnable() {
				
				@Override
				public void run() {
					ln.createLinkNode();
					ln.updatePedestrianNetwork();
				}
			});
			
			t6.start();
			break;
			
		/**
		 * Parsing Openstreetmap data; inserting nodes and edges into the database.
		 */
		case "pbfparser": case "4":
			if(db == null)
				db = new DBConnector();
			
			System.out.println("[INFO] Command: parse PBF file. (\"" + args[0] + "\")");
			System.out.println("[INFO] Source file: " + args[3]);
			System.out.println("[INFO] City: " + args[2].substring(0,1).toUpperCase() + args[2].substring(1, args[2].length()));
			
			//parse the file for the corresponding city
//			GraphBuilder gb = new GraphBuilder(parser.getAllNodes(), parser.getWaysBlocks(), args[1], args[3], db);
			GraphBuilder gb = new GraphBuilder(args[3], args[1], args[2], db);
			Graph g = new Graph(gb);
//			gb.parsePBF();
//			db.emptyStreetNodesTable();
			db.resetFile(args[1] + "/" + args[2] + "_street_nodes_import.sql");
			db.resetFile(args[1] + "/" + args[2] + "_street_edges_import.sql");
			File f1 = new File(args[1] + "/" + args[2] + "_street_nodes_import.sql");
			File f2 = new File(args[1] + "/" + args[2] + "_street_edges_import.sql");
			db.openWriter(f1.getAbsolutePath(), true);
			db.deleteClause(args[2] + "_street_nodes");
			db.closeWriter();
			db.openWriter(f2.getAbsolutePath(), true);
			db.deleteClause(args[2] + "_street_edges");
			db.closeWriter();
			gb.parsePBF();
//			g.buildGraph();
			g.printGraph();
//			System.out.println("[INFO] Graph: " + gb.nrNodes + " nodes, " + gb.nrEdges + " edges");
			break;
			
		/**
		 * Generate the script to add only street network for a city.
		 */
		case "scriptgen": case "5":
			if(db == null)
				db = new DBConnector(true);
			
			System.out.println("[INFO] Command: script generator. (\"" + args[0] + "\")");
			System.out.println("[INFO] Output directory: " + args[1]);
			System.out.println("[INFO] City: " + args[2].substring(0,1).toUpperCase() + args[2].substring(1, args[2].length()));
			
			//generate the script to create the table for the city typed.
			ScriptGenerator sg = new ScriptGenerator(generateTables(args[2], "time-exp"), null, args[1] + "/" + args[2] + "_street_network.sql");
//			sg.setSchemaName("time_expanded");
//			sg.createScript();
			sg.createByReplace(SQLText.BUILD_STREET_NETWORK, "<city>", args[2]);
			sg.writeScipt();
//			try {
//				BufferedWriter bw = new BufferedWriter(new FileWriter(args[1] + "/" + args[2] + "_street_network.sql"));
//				bw.write(sg.getScript());
//				bw.flush();
//				bw.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			
			//parse the file for the corresponding city
//			parser = new PBFParser(args[3]);
//			parser.parsePBF();
//			gb = new GraphBuilder(parser.getAllNodes(), parser.getAllWays(), args[1], args[2], db);
//			g = new Graph(gb);
//			g.buildGraph();
//			g.printGraph();
			break;
			
		/**
		 * Generate le	
		 */
		case "busnet": case "6":
			System.out.println("[INFO] Command: script generator. (\"" + args[0] + "\")");
			System.out.println("[INFO] Output directory: " + args[1]);
			System.out.println("[INFO] City: " + args[2].substring(0,1).toUpperCase() + args[2].substring(1, args[2].length()));
			sg = new ScriptGenerator(args[1] + "/" + args[2] + "_create_bus_nodes_edges.sql");
			sg.createBusScript(args[2], ViewText.BUS_NODES_EDGES);
			sg.writeScipt();
			sg.closeWriter();
			sg = new ScriptGenerator(args[1] + "/" + args[2] + "_bus_trips_import.sql");
			sg.createBusScript(args[2], SQLText.BUS_TRIPS_IMPORT);
			sg.writeScipt();
			sg.closeWriter();
			sg = new ScriptGenerator(args[1] + "/" + args[2] + "_bus_network.sql");
			sg.createByReplace(SQLText.BUILD_BUS_NETWORK, "<city>", args[2]);
			sg.writeScipt();
			sg.closeWriter();
//			try {
//				sg.createBusScript(args[2], ViewText.BUS_NODES_EDGES);
//				BufferedWriter bw = new BufferedWriter(new FileWriter(args[1] + "/" + args[2] + "_create_bus_nodes_edges.sql"));
//				bw.write(sg.getScript());
//				bw.flush();
//				bw.close();
//				sg.createBusScript(args[2], ViewText.BUS_TRIPS_IMPORT);
//				bw = new BufferedWriter(new FileWriter(args[1] + "/" + args[2] + "_bus_trips_import.sql"));
//				bw.write(sg.getScript());
//				bw.flush();
//				bw.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			break;
			
		case "test": case "7":
			sg = new ScriptGenerator(generateTables(args[2], "time-dep"), null, args[1] + "/" + args[2] + "-time-dep-create.sql");
			sg.createScript();
			sg.writeScipt();
//			try {
//				BufferedWriter bw = new BufferedWriter(new FileWriter(args[1] + "/" + args[2] + "-time-dep-create.sql"));
//				sg.createScript();
//				bw.write(sg.getScript());
//				bw.flush();
//				bw.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			break;
		}
		
		
	}
	
	/**
	 * 
	 * @param city
	 * @param db
	 * @return
	 */
	public static ArrayList<Table> generateTables(String city, String db){
		ArrayList<Table> tables = new ArrayList<Table>();
		ArrayList<String> primaryKeys = new ArrayList<String>();
		ArrayList<String> foreignKeys = new ArrayList<String>();
		HashMap<String, String> attributes = new HashMap<String, String>();
		
		switch(db){
		
		case "time-exp":
			//creating Table object for street nodes.
			primaryKeys.add("node_id");
			attributes.put("node_id", TimeExpTablesDescription.StreetNodes.NODE_ID);
			attributes.put("node_in_degree", TimeExpTablesDescription.StreetNodes.NODE_IN_DEGREE);
			attributes.put("node_out_degree", TimeExpTablesDescription.StreetNodes.NODE_OUT_DEGREE);
			attributes.put("node_geometry", TimeExpTablesDescription.StreetNodes.NODE_GEOMETRY);
			Table table = new Table(city + "_street_nodes", primaryKeys, foreignKeys, attributes);
			table.setSchemaName(TimeExpTablesDescription.SCHEMA_NAME);
			tables.add(table);
			
			//creating Table object for street edges.
			primaryKeys = new ArrayList<String>();
			foreignKeys = new ArrayList<String>();
			attributes = new HashMap<String, String>();
			primaryKeys.add("edge_id");
			foreignKeys.add("edge_source;time_expanded;" + city + "_street_nodes;node_id");
			foreignKeys.add("edge_destination;time_expanded;" + city + "_street_nodes;node_id");
			attributes.put("edge_id", TimeExpTablesDescription.StreetEdges.EDGE_ID);
			attributes.put("edge_source", TimeExpTablesDescription.StreetEdges.EDGE_SOURCE);
			attributes.put("edge_destination",TimeExpTablesDescription.StreetEdges.EDGE_DESTINATION);
			attributes.put("edge_geometry", TimeExpTablesDescription.StreetEdges.EDGE_GEOMETRY);
			table = new Table(city + "_street_edges", primaryKeys, foreignKeys, attributes);
			table.setSchemaName(TimeExpTablesDescription.SCHEMA_NAME);
			tables.add(table);
		break;
		
		case "time-dep":
			//Creating Table object for nodes.
			primaryKeys = new ArrayList<String>();
			foreignKeys = new ArrayList<String>();
			attributes = new HashMap<String, String>();
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
			table = new Table(city + "_nodes", primaryKeys, foreignKeys, attributes);
			table.setSchemaName(TimeDepTablesDescription.SCHEMA_NAME);
			tables.add(table);
			
			//Creating Table object for nodes.
			primaryKeys = new ArrayList<String>();
			foreignKeys = new ArrayList<String>();
			attributes = new HashMap<String, String>();
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
			table = new Table(city + "_edges", primaryKeys, foreignKeys, attributes);
			table.setSchemaName(TimeDepTablesDescription.SCHEMA_NAME);
			tables.add(table);
			
			//Creating table object for routes.
			primaryKeys = new ArrayList<String>();
			foreignKeys = new ArrayList<String>();
			attributes = new HashMap<String, String>();
			primaryKeys.add("route_id");
			attributes.put("route_id", TimeDepTablesDescription.Routes.ROUTE_ID);
			attributes.put("route_short_name", TimeDepTablesDescription.Routes.ROUTE_SHORT_NAME);
			attributes.put("route_long_name", TimeDepTablesDescription.Routes.ROUTE_LONG_NAME);
			attributes.put("route_desc", TimeDepTablesDescription.Routes.ROUTE_DESC);
			attributes.put("route_type", TimeDepTablesDescription.Routes.ROUTE_TYPE);
			attributes.put("route_agency_id", TimeDepTablesDescription.Routes.ROUTE_AGENCY_ID);
			table = new Table(city + "_routes", primaryKeys, foreignKeys, attributes);
			table.setSchemaName(TimeDepTablesDescription.SCHEMA_NAME);
			tables.add(table);
			
			//Creating table object for trips.
			primaryKeys = new ArrayList<String>();
			foreignKeys = new ArrayList<String>();
			attributes = new HashMap<String, String>();
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
			table = new Table(city + "_trips", primaryKeys, foreignKeys, attributes);
			table.setSchemaName(TimeDepTablesDescription.SCHEMA_NAME);
			tables.add(table);
			
			//Creating table object for calendar.
			primaryKeys = new ArrayList<String>();
			foreignKeys = new ArrayList<String>();
			attributes = new HashMap<String, String>();
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
			table = new Table(city + "_calendar", primaryKeys, foreignKeys, attributes);
			table.setSchemaName(TimeDepTablesDescription.SCHEMA_NAME);
			tables.add(table);
		}
		
		return tables;
	}

}
