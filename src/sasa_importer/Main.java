package sasa_importer;

import java.io.File;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import sasa_importer.database.DBConnector;
import sasa_importer.street_network.GraphBuilder;
import sasa_importer.street_network.PBFParser;
import sasa_importer.street_network.components.Graph;
import sasa_importer.database.Table;
import sasa_importer.database.TablesDescription;
import sasa_importer.database.ScriptGenerator;

public class Main {
	
	public static void main(String[] args){
		System.gc();
		DBConnector db = null;
		switch (Integer.parseInt(args[0])){
		
		/**
		 * Renaming the vdv file from *.x10 to *.X10.
		 */
		case 1:
			System.out.println("File rename.");
			File vdv = new File("/var/lib/postgresql/bz_database/vdv/");
//			File vdv = new File("C:\\Users\\Luca\\Dropbox\\Uni\\Thesis\\Isochrones\\vdv\\");
			File[] files = vdv.listFiles();
			for(File f : files){
				StringTokenizer st = new StringTokenizer(f.getName(), ".");
				String name = st.nextToken();
				f.renameTo(new File("/var/lib/postgresql/bz_database/vdv/" + name + ".x10"));
//				f.renameTo(new File("C:\\Users\\Luca\\Dropbox\\Uni\\Thesis\\Isochrones\\vdv\\" + name + ".x10"));
			}
			break;
			
		/**
		 * Populating temporary bus database.
		 */
		case 2:
			db = new DBConnector();
			db.emptyTmpDatabase();
			final BusDataParser bdp = new BusDataParser(db);
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
		case 3:
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
		case 4:
			if(db == null)
				db = new DBConnector();
			PBFParser parser = new PBFParser(args[1].replace("\\", "\\\\"));
			parser.parsePBF();
			GraphBuilder gb = new GraphBuilder(parser.getAllNodes(), parser.getAllWays(), args[2], db);
			Graph g = new Graph(gb);
			db.emptyStreetNodesTable();
			g.buildGraph();
			g.printGraph();
			break;
			
		/**
		 * Generate the script to add only street network for a city.
		 */
		case 5:
			if(db == null)
				db = new DBConnector();
			
			//generate the script to create the table for the city typed.
			ScriptGenerator sg = new ScriptGenerator(generateTables(args[2]), null);
			sg.setSchemaName("isochrones_2014");
			sg.createScript();
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(args[2] + "_street_network.sql"));
				bw.write(sg.getScript());
				bw.flush();
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//parse the file for the corresponding city
			parser = new PBFParser(args[1].replace("\\", "\\\\"));
			parser.parsePBF();
			gb = new GraphBuilder(parser.getAllNodes(), parser.getAllWays(), args[2], db);
			g = new Graph(gb);
			g.buildGraph();
			g.printGraph();
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
		table.setSchemaName("isochrones_2014");
		tables.add(table);
		
		//creating Table object for street edges.
		primaryKeys = new ArrayList<String>();
		foreignKeys = new ArrayList<String>();
		attributes = new HashMap<String, String>();
		primaryKeys.add("edge_id");
		foreignKeys.add("edge_source;isochrones_2014;" + city + "_street_nodes;node_id");
		foreignKeys.add("edge_destination;isochrones_2014;" + city + "_street_nodes;node_id");
		attributes.put("edge_id", TablesDescription.StreetEdges.EDGE_ID);
		attributes.put("edge_source", TablesDescription.StreetEdges.EDGE_SOURCE);
		attributes.put("edge_destination",TablesDescription.StreetEdges.EDGE_DESTINATION);
		attributes.put("edge_geometry", TablesDescription.StreetEdges.EDGE_GEOMETRY);
		table = new Table(city + "_street_edges", primaryKeys, foreignKeys, attributes);
		table.setSchemaName("isochrones_2014");
		tables.add(table);
		
		return tables;
	}

}
