package time_expanded_spatial_data.database;

import java.io.BufferedWriter;
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

import time_expanded_spatial_data.bus_network.Service;
import time_expanded_spatial_data.street_network.components.Edge;
import time_expanded_spatial_data.street_network.components.RealNode;

public class DBConnector {
	
	private final static String MAPS_DB_LOCAL = "jdbc:postgresql://localhost:5432/isochrones2014";
	private final static String MAPS_DB_REMOTE = "jdbc:postgresql://maps.inf.unibz.it:5432/isochrones2014";
	private final static String MAPS_USER = "postgres";
	private final static String MAPS_PWD_REMOTE = "AifaXub2";
	private final static String MAPS_PWD_LOCAL = "postgres";
	private static Connection conn;

	public DBConnector(){
		try {
			Class.forName("org.postgresql.Driver");
				conn = DriverManager.getConnection(MAPS_DB_REMOTE, MAPS_USER, MAPS_PWD_REMOTE);
		} catch (SQLException e) {
			System.out.println("Remote database not available. Switching to local.");
			try{
				conn = DriverManager.getConnection(MAPS_DB_LOCAL, MAPS_USER, MAPS_PWD_LOCAL);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
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
			stmt = conn.prepareStatement("INSERT INTO vdv_gtfs_tmp.stops VALUES (?,?,?,?);");
			stmt.setInt(1, id);
			stmt.setString(2, name);
			stmt.setDouble(3, lat);
			stmt.setDouble(4, longi);
			stmt.execute();
		} catch (SQLException e) {
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
	
	public void insertService(int id, String start, String end, String vector){
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("INSERT INTO bz_isochrones_2014.bz_bus_calendar(service_id, service_start_date, service_end_date, service_vector) VALUES(?,?,?,?);");
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
		try {
			stmt = conn.createStatement();
			stmt.execute("DELETE FROM vdv_gtfs_tmp.stops;");
			stmt.execute("DELETE FROM vdv_gtfs_tmp.stop_times;");
			stmt.execute("DELETE FROM vdv_gtfs_tmp.routes;");
			stmt.execute("DELETE FROM vdv_gtfs_tmp.calendar;");
			stmt.execute("DELETE FROM vdv_gtfs_tmp.trips;");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void insertBusNode( double latitude, double longitude, int route){
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("INSERT INTO isochrones_2014.bz_bus_nodes(node_mode, node_route_id, node_geometry)"
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
	
	public void emptyBzBusNode(){
		Statement stmt;
		try {
			stmt = conn.createStatement();
			stmt.execute("DELETE FROM bz_isochrones_2014.bz_bus_nodes;");
			stmt.execute("ALTER SEQUENCE bz_isochrones_2014.bz_bus_nodes_node_id_seq RESTART WITH 1;");
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
			PreparedStatement stmt = conn.prepareStatement("DELETE FROM bz_isochrones_2014.bz_pedestrian_nodes CASCADE;");
			result = stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public boolean insertStreetNode(RealNode aNode, String city){
		boolean result = false;
		try {
			PreparedStatement stmt = conn.prepareStatement("INSERT INTO isochrones_2014." + city + "_street_nodes (node_id, node_geometry) "
					+ "VALUES (?, ST_GeomFromText(?));");
			stmt.setLong(1, aNode.getId());
			stmt.setString(2, aNode.getGeometry().toString());
			result = stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public boolean insertStreetEdge(Edge anEdge, String city){
		boolean result = false;
		try {
			PreparedStatement stmt = conn.prepareStatement("INSERT INTO isochrones_2014." + city + "_street_edges (edge_id, edge_source, edge_destination,edge_geometry) "
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
		int count = 0;
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(city + "_street_nodes_import.sql"));
			String script = "DELETE FROM isochrones_2014." + city + "_street_nodes;\n";
			bw.write(script);
			bw.flush();
			for(RealNode rn : nodes){
//				PreparedStatement stmt = conn.prepareStatement("DELETE FROM bz_isochrones_2014.bz_pedestrian_nodes");
//				stmt.execute();
//				stmt = conn.prepareStatement("INSERT INTO bz_isochrones_2014.bz_pedestrian_nodes (node_id, node_geometry) "
//						+ "VALUES (?, ST_GeomFromEWKT(?));");
//				stmt.setLong(1, rn.getId());
//				stmt.setString(2, rn.getGeometry().toString());
//				result = stmt.execute();
				script = "INSERT INTO isochrones_2014." + city + "_street_nodes (node_id, node_geometry) "
						+ "VALUES ('" + rn.getId() + "', ST_GeomFromEWKT('" + rn.getGeometry().toString() + "'))";
				bw.write(script);
				bw.write(";\n");
				bw.flush();
				if(count % 50 == 0)
					System.out.println("Checkpoint " + count);
				count++;
			}
			bw.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public boolean insertMultipleStreetEdges(Collection<Edge> edges, String city){
		boolean result = false;
		int count = 0;
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(city + "_street_edges_import.sql"));
			String script = "DELETE FROM isochrones_2014." + city + "_street_edges;\n";
			bw.write(script);
			bw.flush();
			for(Edge anEdge : edges){
//				PreparedStatement stmt = conn.prepareStatement("DELETE FROM bz_isochrones_2014.bz_pedestrian_edges");
//				stmt.execute();
//				stmt = conn.prepareStatement("INSERT INTO bz_isochrones_2014.bz_pedestrian_edges (edge_id, edge_source, edge_destination,edge_geometry) "
//						+ "VALUES (?, ?, ?, ST_GeomFromEWKT(?));");
//				stmt.setLong(1, anEdge.getId());
//				stmt.setLong(2, anEdge.getSource());
//				stmt.setLong(3, anEdge.getDestination());
//				stmt.setString(4, anEdge.getGeometry().toString());
				script = "INSERT INTO isochrones_2014." + city + "_street_edges (edge_source, edge_destination,edge_geometry) "
						+ "VALUES ('"+ anEdge.getSource() +"', '" + anEdge.getDestination() +"', ST_GeomFromEWKT('" + anEdge.getGeometry().toString() + "'))";
//				result = stmt.execute();
				bw.write(script);
				bw.write(";\n");
				bw.flush();
				if(count % 50 == 0)
					System.out.println("Checkpoint " + count);
				count++;
			}
			bw.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}	
	
	public int getLastPedestrianNodeID(){
		try {
			PreparedStatement stmt = conn.prepareStatement("SELECT node_id FROM bz_isochrones_2014.bz_pedestrian_nodes ORDER BY node_id;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = stmt.executeQuery();
			if(rs.last()){
				return rs.getInt("node_id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public int getLastPedestrianEdgeID(){
		try {
			PreparedStatement stmt = conn.prepareStatement("SELECT edge_id FROM bz_isochrones_2014.bz_pedestrian_edges ORDER BY edge_id;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
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
		System.out.println(sql);
		try {
			ResultSet rs = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery();
			if(rs != null)
				return rs;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
