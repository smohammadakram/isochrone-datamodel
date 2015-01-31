package time_expanded_spatial_data;

import java.io.File;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import time_expanded_spatial_data.bus_network.BusDataParser;
import time_expanded_spatial_data.database.DBConnector;
import time_expanded_spatial_data.database.ScriptGenerator;
import time_expanded_spatial_data.database.Table;
import time_expanded_spatial_data.database.TablesDescription;
import time_expanded_spatial_data.link_netowrk.LinkNetwork;
import time_expanded_spatial_data.street_network.GraphBuilder;
import time_expanded_spatial_data.street_network.PBFParser;
import time_expanded_spatial_data.street_network.components.Graph;

public class Main {
	
	public static void main(String[] args){
		System.gc();
		DBConnector db = null;
		switch (args[0]){
		
		/**
		 * Renaming the vdv file from *.x10 to *.X10.
		 */
		case "vdvnames":
			System.out.println("[INFO] File rename.");
			File vdv = new File("vdv/");
			File[] files = vdv.listFiles();
			for(File f : files){
				StringTokenizer st = new StringTokenizer(f.getName(), ".");
				String name = st.nextToken();
				f.renameTo(new File(args[1]+ name + ".X10"));
			}
			break;
			
		/**
		 * Populating temporary bus database.
		 */
		case "tmpdb":
			db = new DBConnector();
			db.emptyTmpDatabase();
			final BusDataParser bdp = new BusDataParser(db);
			System.out.println("[INFO] Temporal database creation.");
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
		case "linknet":
			db = new DBConnector();
			final LinkNetwork ln = new LinkNetwork(db);
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
		case "pbfparser":
			if(db == null)
				db = new DBConnector();
			
			System.out.println("[INFO] Command: parse PBF file. (\"" + args[0] + "\")");
			System.out.println("[INFO] Source file: " + args[3]);
			System.out.println("[INFO] City: " + args[2].substring(0,1).toUpperCase() + args[2].substring(1, args[2].length()));
			
			//parse the file for the corresponding city
//			GraphBuilder gb = new GraphBuilder(parser.getAllNodes(), parser.getWaysBlocks(), args[1], args[3], db);
			GraphBuilder gb = new GraphBuilder(args[3], args[1], args[2], db);
			gb.parsePBF();
			Graph g = new Graph(gb);
//			db.emptyStreetNodesTable();
			g.buildGraph();
			g.printGraph();
			break;
			
		/**
		 * Generate the script to add only street network for a city.
		 */
		case "scriptgen":
			if(db == null)
				db = new DBConnector(true);
			
			System.out.println("[INFO] Command: script generator. (\"" + args[0] + "\")");
			System.out.println("[INFO] Output directory: " + args[1]);
			System.out.println("[INFO] City: " + args[2].substring(0,1).toUpperCase() + args[2].substring(1, args[2].length()));
			
			//generate the script to create the table for the city typed.
			ScriptGenerator sg = new ScriptGenerator(generateTables(args[2]), null);
			sg.setSchemaName("isochrones_2014");
			sg.createScript();
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(args[1] + "/" + args[2] + "_street_network.sql"));
				System.out.println("[INFO] SQL directory: " + args[1]);
				bw.write(sg.getScript());
				bw.flush();
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//parse the file for the corresponding city
//			parser = new PBFParser(args[3]);
//			parser.parsePBF();
//			gb = new GraphBuilder(parser.getAllNodes(), parser.getAllWays(), args[1], args[2], db);
//			g = new Graph(gb);
//			g.buildGraph();
//			g.printGraph();
			break;
		}
	}
	
	public static ArrayList<Table> generateTables(String city){
		ArrayList<Table> tables = new ArrayList<Table>();
		ArrayList<String> primaryKeys = new ArrayList<String>();
		ArrayList<String> foreignKeys = new ArrayList<String>();
		HashMap<String, String> attributes = new HashMap<String, String>();
		
		//creating Table object for street nodes.
		primaryKeys.add("node_id");
		attributes.put("node_id", TablesDescription.StreetNodes.NODE_ID);
		attributes.put("node_in_degree", TablesDescription.StreetNodes.NODE_IN_DEGREE);
		attributes.put("node_out_degree", TablesDescription.StreetNodes.NODE_OUT_DEGREE);
		attributes.put("node_geometry", TablesDescription.StreetNodes.NODE_GEOMETRY);
		Table table = new Table(city + "_street_nodes", primaryKeys, foreignKeys, attributes);
		table.setSchemaName("time_expanded");
		tables.add(table);
		
		//creating Table object for street edges.
		primaryKeys = new ArrayList<String>();
		foreignKeys = new ArrayList<String>();
		attributes = new HashMap<String, String>();
		primaryKeys.add("edge_id");
		foreignKeys.add("edge_source;time_expanded;" + city + "_street_nodes;node_id");
		foreignKeys.add("edge_destination;time_expanded;" + city + "_street_nodes;node_id");
		attributes.put("edge_id", TablesDescription.StreetEdges.EDGE_ID);
		attributes.put("edge_source", TablesDescription.StreetEdges.EDGE_SOURCE);
		attributes.put("edge_destination",TablesDescription.StreetEdges.EDGE_DESTINATION);
		attributes.put("edge_geometry", TablesDescription.StreetEdges.EDGE_GEOMETRY);
		table = new Table(city + "_street_edges", primaryKeys, foreignKeys, attributes);
		table.setSchemaName("time_expanded");
		tables.add(table);
		
		return tables;
	}

}
