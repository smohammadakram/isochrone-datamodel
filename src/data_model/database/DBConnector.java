package data_model.database;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import data_model.dijkstra.BusEdge;
import data_model.dijkstra.TDEdge;
import data_model.dijkstra.Vertex;
import data_model.dijkstra.BusVertex;
import data_model.time_dependent.database.TimeDepTablesDescription;
import data_model.time_expanded.bus_network.Service;
import data_model.time_expanded.database.TimeExpTablesDescription;
import data_model.time_expanded.link_netowrk.LinkEdge;
import data_model.time_expanded.street_network.components.Edge;
import data_model.time_expanded.street_network.components.RealNode;

public class DBConnector {
	
	private final static String MAPS_DB_LOCAL = "jdbc:postgresql://localhost:5432/spatial";
//	private final static String MAPS_DB_REMOTE = "jdbc:postgresql://maps.inf.unibz.it:5432/isochrones2014";
	private final static String MAPS_USER = "spatial";
//	private final static String MAPS_PWD_REMOTE = "AifaXub2";
	private final static String MAPS_PWD_LOCAL = "spatial";
	private static Connection conn;
	private BufferedWriter bw;
	private String sqlDirectory;
	private int checkpoint = 0;

	public DBConnector(){
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(MAPS_DB_LOCAL, MAPS_USER, MAPS_PWD_LOCAL);
		} catch (SQLException e) {
			System.out.println("[ERROR] Database \"spatial\" does not exist. Please create it.");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public DBConnector(boolean tmp){}
	
	public static Connection getConnection(){
		return conn;
	}
	
	public static void closeConnection(){
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public int getResultSetSize(ResultSet rs){
		try {
			rs.last();
			return rs.getRow();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public String insertStop(int id, String name, double lat, double longi){
		PreparedStatement stmt = null;
		try {
			bw = new BufferedWriter(new FileWriter(sqlDirectory + "/vdv-stops.sql"));
			bw.write("DELETE FROM vdv_gtfs_tmp.stops CASCADE;");
			stmt = conn.prepareStatement("INSERT INTO vdv_gtfs_tmp.stops VALUES (?,?,?,?);");
			stmt.setInt(1, id);
			stmt.setString(2, name);
			stmt.setDouble(3, lat);
			stmt.setDouble(4, longi);
			bw.write(stmt.toString());
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stmt.toString();
	}
	
	public void insertTrip(int routeID, int tripID, int serviceID){
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("INSERT INTO vdv_gtfs_tmp.trips VALUES (?,?,?);");
			stmt.setInt(1, routeID);
			stmt.setInt(2, tripID);
			stmt.setInt(3, serviceID);
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String insertStopTime(int tripID, int stopID, String arrivalTime, String departureTime, int seqNr){
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement("INSERT INTO vdv_gtfs_tmp.stop_times VALUES (?,?,?,?,?);");
			stmt.setInt(1, tripID);
			stmt.setInt(2, stopID);
			stmt.setString(3, arrivalTime);
			stmt.setString(4, departureTime);
			stmt.setInt(5, seqNr);
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return stmt.toString();
	}
	
	public void insertRoute(int id, String longName, String shortName){
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("INSERT INTO vdv_gtfs_tmp.routes VALUES(?,?,?);");
			stmt.setInt(1, id);
			stmt.setString(2, longName);
			stmt.setString(3, shortName);
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void insertCalendar(int service, boolean[] validity, String startDate, String endDate){
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("INSERT INTO vdv_gtfs_tmp.calendar VALUES(?,?,?,?,?,?,?,?,?,?);");
			stmt.setInt(1, service);
			for(int i = 2; i < 9; i++)
				stmt.setBoolean(i, validity[i-2]);
			stmt.setString(9, startDate);
			stmt.setString(10, endDate);
			stmt.execute();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void insertService(int id, String start, String end, String vector, String city){
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("INSERT INTO " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_bus_calendar(service_id, service_start_date, service_end_date, service_vector) VALUES(?,?,?,?);");
			stmt.setInt(1, id);
			stmt.setString(2, start);
			stmt.setString(3, end);
			stmt.setString(4, vector);
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void emptyTmpDatabase(){
		Statement stmt;
		boolean result = false;
		try {
			stmt = conn.createStatement();
			result = stmt.execute("DELETE FROM vdv_gtfs_tmp.stops CASCADE;");
			if(result)
				System.out.println("[INFO] vdv_gtfs_tmp.stops has been emptied.");
			else
				System.out.println("[ERROR] An error occured. vdv_gtfs_tmp.stops not empty.");
			result = stmt.execute("DELETE FROM vdv_gtfs_tmp.stop_times CASCADE;");
			if(result)
				System.out.println("[INFO] vdv_gtfs_tmp.stops_times has been emptied.");
			else
				System.out.println("[ERROR] An error occured. vdv_gtfs_tmp.stops_times not empty.");
			result = stmt.execute("DELETE FROM vdv_gtfs_tmp.routes CASCADE;");
			if(result)
				System.out.println("[INFO] vdv_gtfs_tmp.routes has been emptied.");
			else
				System.out.println("[ERROR] An error occured. vdv_gtfs_tmp.routes not empty.");
			result = stmt.execute("DELETE FROM vdv_gtfs_tmp.calendar CASCADE;");
			if(result)
				System.out.println("[INFO] vdv_gtfs_tmp.calendar has been emptied.");
			else
				System.out.println("[ERROR] An error occured. vdv_gtfs_tmp.calendar not empty.");
			result = stmt.execute("DELETE FROM vdv_gtfs_tmp.trips CASCADE;");
			if(result)
				System.out.println("[INFO] vdv_gtfs_tmp.trips has been emptied.");
			else
				System.out.println("[ERROR] An error occured. vdv_gtfs_tmp.trips not empty.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void insertBusNode( double latitude, double longitude, int route, String city){
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("INSERT INTO " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_bus_nodes(node_mode, node_route_id, node_geometry)"
					+ " VALUES (?,?,ST_AsEWKT(ST_SetSRID(ST_Makepoint(?,?), ?)));");
			stmt.setInt(1, 0);
			stmt.setInt(2, route);
			stmt.setDouble(3, longitude);
			stmt.setDouble(4, latitude);
			stmt.setInt(5, 4326);
			stmt.execute();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void emptyBzBusNode(String city){
		Statement stmt;
		try {
			stmt = conn.createStatement();
			stmt.execute("DELETE FROM " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_bus_nodes;");
			stmt.execute("ALTER SEQUENCE " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_bus_nodes_node_id_seq RESTART WITH 1;");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<Service> getCalendars(){
		List<Service> services = null;
		try {
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM vdv_gtfs_tmp.calendar;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = stmt.executeQuery();
			if(rs.first()){
				services = new ArrayList<Service>();
				while(rs.next()){
					Service s = new Service();
					s.setId(rs.getInt(1));
					s.setStartDate(rs.getString(2));
					s.setEndDate(rs.getString(3));
					s.setValidity(rs.getBoolean(4), rs.getBoolean(5), rs.getBoolean(6), rs.getBoolean(7), rs.getBoolean(8), rs.getBoolean(9), rs.getBoolean(10));
					services.add(s);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return services;
	}
	
	public boolean emptyStreetNodesTable(){
		boolean result = false;
		try {
			PreparedStatement stmt = conn.prepareStatement("DELETE FROM " + TimeExpTablesDescription.SCHEMA_NAME + ".bz_pedestrian_nodes CASCADE;");
			result = stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public boolean insertStreetNode(RealNode aNode, String city){
		boolean result = false;
		try {
			PreparedStatement stmt = conn.prepareStatement("INSERT INTO " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_street_nodes (node_id, node_geometry) "
					+ "VALUES (?, ST_GeomFromText(?));");
			stmt.setLong(1, aNode.getId());
			stmt.setString(2, aNode.getGeometry().toString());
			result = stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public boolean insertStreetNode(long id, String geom, String city){
		boolean result = false;
		try {
			PreparedStatement stmt = conn.prepareStatement("INSERT INTO " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_street_nodes(node_id, node_geometry) "
					+ "VALUES (?, ST_GeomFromEWKT(?));");

			stmt.setLong(1, id);
			stmt.setString(2, geom.toString());
			result = stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public boolean insertStreetEdge(Edge anEdge, String city){
		boolean result = false;
		try {
			PreparedStatement stmt = conn.prepareStatement("INSERT INTO " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_street_edges (edge_id, edge_source, edge_destination,edge_geometry) "
					+ "VALUES (?, ?, ?, ST_GeomFromText(?)));");
			stmt.setLong(1, anEdge.getId());
			stmt.setLong(2, anEdge.getSource());
			stmt.setLong(3, anEdge.getDestination());
			stmt.setString(4, anEdge.getGeometry().toString());
			result = stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public boolean insertMultipleStreetNodes(Collection<RealNode> nodes, String city){
		boolean result = false;
		try {
			String script = "";
			for(RealNode rn : nodes){
//				PreparedStatement stmt = conn.prepareStatement("DELETE FROM bz_isochrones_2014.bz_pedestrian_nodes");
//				stmt.execute();
//				stmt = conn.prepareStatement("INSERT INTO bz_isochrones_2014.bz_pedestrian_nodes (node_id, node_geometry) "
//						+ "VALUES (?, ST_GeomFromEWKT(?));");
//				stmt.setLong(1, rn.getId());
//				stmt.setString(2, rn.getGeometry().toString());
//				result = stmt.execute();
				script = "INSERT INTO " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_street_nodes (node_id, node_geometry) "
						+ "VALUES ('" + rn.getId() + "', ST_GeomFromEWKT('" + rn.getGeometry().toString() + "'))";
				bw.write(script);
				bw.write(";\n");
				bw.flush();
//				if(checkpoint % 50 == 0)
//					System.out.println("[INFO] Checkpoint " + checkpoint);
//				checkpoint++;
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public boolean insertMultipleStreetEdges(Collection<Edge> edges, String city){
		boolean result = false;
		try {
			for(Edge anEdge : edges){
				String script ="";
//				PreparedStatement stmt = conn.prepareStatement("DELETE FROM bz_isochrones_2014.bz_pedestrian_edges");
//				stmt.execute();
//				stmt = conn.prepareStatement("INSERT INTO bz_isochrones_2014.bz_pedestrian_edges (edge_id, edge_source, edge_destination,edge_geometry) "
//						+ "VALUES (?, ?, ?, ST_GeomFromEWKT(?));");
//				stmt.setLong(1, anEdge.getId());
//				stmt.setLong(2, anEdge.getSource());
//				stmt.setLong(3, anEdge.getDestination());
//				stmt.setString(4, anEdge.getGeometry().toString());
				script = "INSERT INTO " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_street_edges (edge_source, edge_destination,edge_geometry) "
						+ "VALUES ('"+ anEdge.getSource() +"', '" + anEdge.getDestination() +"', ST_SetSRID(ST_GeomFromEWKT('" + anEdge.getGeometry().toString() + "'), 4326))";
//				result = stmt.execute();
				bw.write(script);
				bw.write(";\n");
				bw.flush();
//				if(checkpoint % 50 == 0)
//					System.out.println("[INFO] Checkpoint " + checkpoint);
//				checkpoint++;
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}	
	
	public long getLastPedestrianNodeID(String city){
		try {
			PreparedStatement stmt = conn.prepareStatement("SELECT node_id FROM " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_street_nodes ORDER BY node_id;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = stmt.executeQuery();
			if(rs.last()){
				return rs.getLong("node_id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public int getLastPedestrianEdgeID(String city){
		try {
			PreparedStatement stmt = conn.prepareStatement("SELECT edge_id FROM " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_street_edges ORDER BY edge_id;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = stmt.executeQuery();
			if(rs.last()){
				return rs.getInt("edge_id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public ResultSet executeSimpleQuery(String sql){
//		System.out.println(sql);
		try {
			PreparedStatement stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
//			System.out.println(stmt.toString());
			ResultSet rs = stmt.executeQuery();
			if(rs != null)
				return rs;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void executeSimpleQueryNoResult(String sql){
		try {
			conn.prepareStatement(sql).execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void resetFile(String file){
		File f = new File(file);
		if(f.exists())
			f.delete();
	}
	
	public void openWriter(String file, boolean append){
		try {
			bw = new BufferedWriter(new FileWriter(file, append));
//			System.out.println("[INFO] Stream opened.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void closeWriter(){
		try {
			bw.close();
//			System.out.println("[INFO] Stream closed.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteClause(String table){
		String script = "DELETE FROM " + TimeExpTablesDescription.SCHEMA_NAME + "." +table + ";\n";
		try {
			bw.write(script);
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void resetCheckpoint(){
		checkpoint = 0;
	}

	public String getSqlDirectory() {
		return sqlDirectory;
	}

	public void setSqlDirectory(String sqlDirectory) {
		this.sqlDirectory = sqlDirectory;
	}
	
	public long getMaxStreetNodeID(String city){
		try {
			PreparedStatement stmt = conn.prepareStatement("SELECT node_id FROM " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_street_nodes ORDER BY node_id DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = stmt.executeQuery();
			if(rs.first()){
				return rs.getLong("node_id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public List<Integer> getBusNodeByGeometry(String geom, String city){
		List<Integer> result = new ArrayList<Integer>();
		try {
			PreparedStatement stmt = conn.prepareStatement("SELECT node_id FROM " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_bus_nodes WHERE node_geometry = ST_GeomFromText(?);", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			stmt.setString(1, geom);
			ResultSet rs = stmt.executeQuery();
			rs.beforeFirst();
			while(rs.next())
				result.add(rs.getInt("node_id"));
				
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public boolean insertLinkEdge(String city, LinkEdge le){
		boolean result = false;
		try {
			PreparedStatement stmt = conn.prepareStatement("INSERT INTO " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_links(link_source, link_source_mode, link_destination, link_destination_mode) VALUES(?,?,?,?)");
			stmt.setLong(1, le.getSource());
			stmt.setInt(2, le.getSourceMode());
			stmt.setLong(3, le.getDestination());
			stmt.setInt(4, le.getDestinationMode());
			result = stmt.execute();				
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public String getGeometryByNodeID(long id, String city){
		String result = "";
		try {
			PreparedStatement stmt = conn.prepareStatement("SELECT node_geometry FROM " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_street_nodes WHERE node_id = ?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			stmt.setLong(1, id);
			ResultSet rs = stmt.executeQuery();
			rs.first();
			result = rs.getString("node_geometry");
				
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public List<Vertex> getTimeExpandedStreetNodes(String city){
		List<Vertex> result = new ArrayList<Vertex>();
		try{
			PreparedStatement stmt = conn.prepareStatement("SELECT node_id FROM " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_street_nodes", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = stmt.executeQuery();
			rs.beforeFirst();
			while(rs.next()){
				String id = String.valueOf(rs.getLong("node_id"));
				Vertex v = new Vertex(id, id);
				result.add(v);
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
		return result;
	}
	
	public List<BusVertex> getTimeExpandedBusNodes(String city){
		List<BusVertex> result = new ArrayList<BusVertex>();
		try{
			PreparedStatement stmt = conn.prepareStatement("SELECT node_id, node_route_id FROM " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_street_nodes", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = stmt.executeQuery();
			rs.beforeFirst();
			while(rs.next()){
				String id = String.valueOf(rs.getLong("node_id"));
				BusVertex v = new BusVertex(id, id, rs.getInt("node_route_id"));
				result.add(v);
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
		return result;
	}
	
	public List<data_model.dijkstra.Edge> getTimeExpandedStreetEdges(String city){
		List<data_model.dijkstra.Edge> result = new ArrayList<data_model.dijkstra.Edge>();
		try{
			PreparedStatement stmt = conn.prepareStatement("SELECT edge_id, edge_source, edge_destination, edge_length FROM " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_street_edges", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = stmt.executeQuery();
			rs.beforeFirst();
			while(rs.next()){
				String sID = String.valueOf(rs.getLong("edge_source"));
				String dID = String.valueOf(rs.getLong("edge_destination"));
				data_model.dijkstra.Edge e = new data_model.dijkstra.Edge(String.valueOf(rs.getLong("edge_id")), new Vertex(sID, sID), new Vertex(dID, dID), ((int) (rs.getDouble("edge_length") / 0.42)));
				result.add(e);
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
		return result;
	}
	
	public List<BusEdge> getTimeExpandedBusEdges(String city){
		List<BusEdge> result = new ArrayList<BusEdge>();
		try{
			PreparedStatement stmt = conn.prepareStatement("SELECT edge_id, edge_source, edge_destination, edge_route FROM " + TimeExpTablesDescription.SCHEMA_NAME + "." + city + "_bus_edges", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = stmt.executeQuery();
			rs.beforeFirst();
			while(rs.next()){
				String sID = String.valueOf(rs.getLong("edge_source"));
				String dID = String.valueOf(rs.getLong("edge_destination"));
				BusEdge e = new BusEdge(String.valueOf(rs.getLong("edge_id")), new Vertex(sID, sID), new Vertex(dID, dID), 0, rs.getInt("edge_route_id"));
				result.add(e);
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
		return result;
	}
	
	public List<Vertex> getTimeDependentNodes(String city){
		List<Vertex> result = new ArrayList<Vertex>();
		try{
			PreparedStatement stmt = conn.prepareStatement("SELECT node_id, node_mode FROM " + TimeDepTablesDescription.SCHEMA_NAME + "." + city + "_nodes", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = stmt.executeQuery();
			rs.beforeFirst();
			while(rs.next()){
				String id = String.valueOf(rs.getLong("node_id"));
				Vertex v = new Vertex(id, id, rs.getInt("node_mode"));
				result.add(v);
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
		return result;
	}
	
	public List<TDEdge> getTimeDependentEdges(String city){
		List<TDEdge> result = new ArrayList<TDEdge>();
		try{
			PreparedStatement stmt = conn.prepareStatement("SELECT edge_id, edge_source, edge_destination, edge_length, edge_mode, edge_source_mode, edge_destination_mode, edge_route_id FROM " + TimeDepTablesDescription.SCHEMA_NAME + "." + city + "_edges", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = stmt.executeQuery();
			rs.beforeFirst();
			while(rs.next()){
				String sID = String.valueOf(rs.getLong("edge_source"));
				String dID = String.valueOf(rs.getLong("edge_destination"));
				int eMode = rs.getInt("edge_mode");
				TDEdge e = null;
				if(eMode == 1)
					e = new TDEdge(String.valueOf(rs.getInt("edge_id")), new Vertex(sID, sID, rs.getInt("edge_source_mode")), new Vertex(dID, dID, rs.getInt("edge_destination_mode")), rs.getDouble("edge_length"), rs.getInt("edge_mode"), rs.getInt("edge_source_mode"), rs.getInt("edge_destination_mode"));
				else
					e = new TDEdge(String.valueOf(rs.getInt("edge_id")), new Vertex(sID, sID, rs.getInt("edge_source_mode")), new Vertex(dID, dID, rs.getInt("edge_destination_mode")), rs.getDouble("edge_length"), rs.getInt("edge_mode"), rs.getInt("edge_source_mode"), rs.getInt("edge_destination_mode"), rs.getInt("edge_route_id"));
				result.add(e);
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
		return result;
	}
	
	
}
