package sasa_importer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DBConnector {
	
	private final static String MAPS_DB = "jdbc:postgresql://localhost:5432/isochrones2014";
//	private final static String MAPS_DB = "jdbc:postgresql://maps.inf.unibz.it:5432/isochrones2014";
	private final static String MAPS_USER = "postgres";
	private final static String MAPS_PWD = "AifaXub2";
//	private final static String MAPS_PWD = "postgres";
	private static Connection conn;

	public DBConnector(){
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(MAPS_DB, MAPS_USER, MAPS_PWD);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
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
	
	public void insertBusNode( double latitude, double longitude, int route){
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("INSERT INTO bz_isochrones_2014.bz_bus_nodes(node_mode, node_route_id, node_geometry)"
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
//			stmt.execute("DELETE FROM bz_isochrones_2014.bz_bus_calendar;");
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
