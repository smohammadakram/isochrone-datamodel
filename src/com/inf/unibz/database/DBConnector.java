package com.inf.unibz.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.inf.unibz.entity.BusRoute;
import com.inf.unibz.entity.BusStop;
import com.inf.unibz.entity.Edge;
import com.inf.unibz.entity.TripConnection;
import com.inf.unibz.entity.Vertex;

public class DBConnector {
	
	private final static String SASA_DB = "jdbc:postgresql://localhost:5432/sasabus";
	private final static String SASA_USER = "sasabus";
	private final static String SASA_PWD = "sasabus";
	private final static String ROUTING_DB = "jdbc:postgresql://localhost:5432/routing";
	private final static String ROUTING_USER = "postgres";
	private final static String ROUTING_PWD = "postgres";
	private final static String MAPS_DB = "jdbc:postgresql://maps.inf.unibz.it:5432/isochrones2014";
	private final static String MAPS_USER = "postgres";
	private final static String MAPS_PWD = "AifaXub2";
	private static Connection conn;
	private PreparedStatement stmt;
	private ResultSet rs;

	public DBConnector(String db){
		try {
			switch(db){
			case "sasa":
				if(conn == null)
					conn = DriverManager.getConnection(SASA_DB, SASA_USER, SASA_PWD);
				break;
			case "routing":
				if(conn == null)
					conn = DriverManager.getConnection(ROUTING_DB, ROUTING_USER, ROUTING_PWD);
				break;
			case "maps":
				conn = DriverManager.getConnection(MAPS_DB, MAPS_USER, MAPS_PWD);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<Vertex> getPedestrianNodes(){
		ResultSet rs;
		ArrayList<Vertex> verteces = new ArrayList<Vertex>();
		try {
			stmt = conn.prepareStatement("SELECT id from bz_nodes where type = ?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			stmt.setString(1, "PED");
			rs = stmt.executeQuery();
			rs.first();
			while(!rs.isAfterLast()){
				Vertex v = new Vertex(rs.getInt(1), "Node " + String.valueOf(rs.getInt(1)));
				verteces.add(v);
				rs.next();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return verteces;
	}
	
	public ArrayList<Edge> getPedestrianEdges(){
		ResultSet rs;
		ArrayList<Edge> edges = new ArrayList<Edge>();
		try {
			stmt = conn.prepareStatement("SELECT id, source, target, length from bz_edges where edge_mode = ? ORDER BY source", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, 0);
			rs = stmt.executeQuery();
			if(rs.first())
				while(!rs.isAfterLast()){
					Vertex source = new Vertex(rs.getInt(2), "Node " + String.valueOf(rs.getInt(2)));
					Vertex destination = new Vertex(rs.getInt(3), "Node " + String.valueOf(rs.getInt(3)));
					Edge v = new Edge(rs.getInt(1), source, destination, (int) rs.getDouble(4));
					edges.add(v);
					rs.next();
				}
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return edges;
	}
	
	public ArrayList<Vertex> getBusNodes(){
		ResultSet rs;
		ArrayList<Vertex> verteces = new ArrayList<Vertex>();
		try {
			stmt = conn.prepareStatement("SELECT id from bz_nodes where type = ? ORDER BY id", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			stmt.setString(1, "BUS");
			rs = stmt.executeQuery();
			rs.first();
			while(!rs.isAfterLast()){
				Vertex v = new Vertex(rs.getInt(1), "Node " + String.valueOf(rs.getInt(1)));
				verteces.add(v);
				rs.next();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return verteces;
	}
	
	public int[] getBusNodesAsArray(){
		int[] verteces = null;
		try {
			stmt = conn.prepareStatement("SELECT id from bz_nodes where type = ? ORDER BY id", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			stmt.setString(1, "BUS");
			rs = stmt.executeQuery();
			rs.first();
			verteces = new int[getResultSetSize(rs)];
			for(int i = 0; !rs.isAfterLast(); i++){
				verteces[i] = rs.getInt(1);
				rs.next();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return verteces;
	}
	
	public ArrayList<Edge> getBusEdges(){
		ResultSet rs;
		ArrayList<Edge> edges = new ArrayList<Edge>();
		try {
			stmt = conn.prepareStatement("SELECT id, source, target, length from bz_edges where edge_mode = ? ORDER BY source", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, 1);
			rs = stmt.executeQuery();
			if(rs.first())
				while(!rs.isAfterLast()){
					Vertex source = new Vertex(rs.getInt(2), "Node " + String.valueOf(rs.getInt(2)));
					Vertex destination = new Vertex(rs.getInt(3), "Node " + String.valueOf(rs.getInt(3)));
					Edge v = new Edge(rs.getInt(1), source, destination, (int) rs.getDouble(4));
					edges.add(v);
					rs.next();
				}
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return edges;
	}
	
	public ArrayList<Vertex> getAllNodes(){
		ResultSet rs;
		ArrayList<Vertex> verteces = new ArrayList<Vertex>();
		try {
			stmt = conn.prepareStatement("SELECT id from bz_nodes", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery();
			rs.first();
			while(!rs.isAfterLast()){
				Vertex v = new Vertex(rs.getInt(1), "Node " + String.valueOf(rs.getInt(1)));
				verteces.add(v);
				rs.next();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return verteces;
	}
	
	public List<Edge> getAllEdges(){
		ResultSet rs;
		List<Edge> edges = new ArrayList<Edge>();
		try {
			stmt = conn.prepareStatement("SELECT id, source, target, length, edge_mode FROM bz_edges ORDER BY id", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery();
			if(rs.first())
				while(!rs.isAfterLast()){
					Vertex source = new Vertex(rs.getInt(2), "Node " + String.valueOf(rs.getInt(2)));
					Vertex destination = new Vertex(rs.getInt(3), "Node " + String.valueOf(rs.getInt(3)));
					Edge v = new Edge(rs.getInt(1), source, destination, (int) rs.getDouble(4));
					v.setType(rs.getInt(5));
					edges.add(v);
					rs.next();
				}
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return edges;
	}
	
	public int[] getAllNodesAsArray(){
		ResultSet rs;
		int[] result = null;
		try {
			stmt = conn.prepareStatement("SELECT id FROM bz_nodes ORDER BY id", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery();
			result = new int[getResultSetSize(rs)];
			rs.first();
			for(int i = 0; !rs.isLast(); i++){
				result[i] = rs.getInt(1);
				rs.next();
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}		
		return result;
	}
	
	public ArrayList<TripConnection> getAllTripConnections(){
		ArrayList<TripConnection> conns = null;
		try{
			stmt = conn.prepareStatement("SELECT edge_id, trip_id, route_id, source, time_d, target, time_a, service_id "
					+ "FROM bz_schedules "
					+ "ORDER BY edge_id, time_d;", 
					ResultSet.TYPE_SCROLL_INSENSITIVE, 
					ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery();
			ResultSet rs = stmt.executeQuery();
			rs.beforeFirst();
			conns = new ArrayList<TripConnection>();
			TripConnection tc;
			while(rs.next()){
				tc = new TripConnection();
				tc.setTripID(rs.getInt(2));
				tc.setDepTime(rs.getString(5));
				tc.setArrTime(rs.getString(7));
				tc.setLineVariant(rs.getInt(3));
				tc.setEdgeID(rs.getInt(1));
				tc.setSourceID(rs.getInt(4));
				tc.setTargetID(rs.getInt(6));
				conns.add(tc);
			}
		}
		catch (SQLException ex) {
			ex.printStackTrace();
		}
		return conns;
	}
	
	public Hashtable<Integer, Edge> getAllEdgesAsHashtable(){
		Hashtable<Integer, Edge> edges = new Hashtable<Integer, Edge>(); 
		try {
			stmt = conn.prepareStatement("SELECT id, source, target, length FROM bz_edges ORDER BY id", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, 0);
			stmt.setInt(2, 0);
			rs = stmt.executeQuery();
			if(rs.first())
				while(!rs.isAfterLast()){
					Vertex source = new Vertex(rs.getInt(2), "Node " + String.valueOf(rs.getInt(2)));
					Vertex destination = new Vertex(rs.getInt(3), "Node " + String.valueOf(rs.getInt(3)));
					Edge v = new Edge(rs.getInt(1), source, destination, (int) rs.getDouble(4));
					edges.put(rs.getInt(1), v);
					rs.next();
				}
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return edges;
	}
	
	public ArrayList<Edge> getAllEdgesBySource(){
		ResultSet rs;
		ArrayList<Edge> edges = new ArrayList<Edge>();
		try {
			stmt = conn.prepareStatement("SELECT id, source, target, length FROM bz_edges ORDER BY source", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, 0);
			stmt.setInt(2, 0);
			rs = stmt.executeQuery();
			if(rs.first())
				while(!rs.isAfterLast()){
					Vertex source = new Vertex(rs.getInt(2), "Node " + String.valueOf(rs.getInt(2)));
					Vertex destination = new Vertex(rs.getInt(3), "Node " + String.valueOf(rs.getInt(3)));
					Edge v = new Edge(rs.getInt(1), source, destination, (int) rs.getDouble(4));
					edges.add(v);
					rs.next();
				}
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return edges;
	}
	
	public static void closeConnection(){
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public Edge getEdgeByExtreme(Vertex source, Vertex target){
		Edge e = null;
		try{
			stmt = conn.prepareStatement("SELECT id, length FROM bz_edges WHERE source = ? AND target = ?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, source.getId());
			stmt.setInt(2, target.getId());
			ResultSet rs = stmt.executeQuery();
			if(getResultSetSize(rs) != 0){
				rs.first();
				e = new Edge(rs.getInt(1), source, target, (int) rs.getDouble(2));
			}
		}
		catch (SQLException ex) {
			ex.printStackTrace();
		}
		return e;
	}
	
	public ArrayList<TripConnection> getTripConnectionsByEdge(int id){
		ArrayList<TripConnection> conns = null;
		try{
			stmt = conn.prepareStatement("SELECT edge_id, trip_id, route_id, source, time_d, target, time_a, service_id "
					+ "FROM bz_schedules "
					+ "WHERE edge_id = ? "
					+ "ORDER BY edge_id, time_d;", 
					ResultSet.TYPE_SCROLL_INSENSITIVE, 
					ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			ResultSet rs = stmt.executeQuery();
			rs.beforeFirst();
			conns = new ArrayList<TripConnection>();
			TripConnection tc;
			while(rs.next()){
				tc = new TripConnection();
				tc.setTripID(rs.getInt(1));
				tc.setDepTime(rs.getString(4));
				tc.setArrTime(rs.getString(6));
				tc.setLineVariant(rs.getInt(2));
				tc.setEdgeID(rs.getInt(0));
				conns.add(tc);
			}
		}
		catch (SQLException ex) {
			ex.printStackTrace();
		}
		System.out.println("Edge: " + id);
		System.out.println("Result size: " + conns.size());
		return null;
	}
	
	public ArrayList<Vertex> getEdgesBySource(int id){
		ArrayList<Vertex> result = null;
		Vertex s = null;
		Vertex v = null;
		Edge e = null;
		try{
			stmt = conn.prepareStatement("SELECT id, target, length FROM bz_edges WHERE source = ?");
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			rs.first();
			result = new ArrayList<Vertex>();
			while(!rs.isAfterLast()){
				s = new Vertex(id, "");
				v = new Vertex(rs.getInt(2), "");
				e = new Edge(rs.getInt(1), s, v, rs.getInt(3));
				result.add(v);
				rs.next();
			}
		}
		catch (SQLException ex) {
			ex.printStackTrace();
		}
		return result;
	}
	
	public int getResultSetSize(ResultSet rs){
		try {
			rs.last();
			return rs.getRow();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
	public void insertStop(int id, String name, double lat, double longi){
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("INSERT INTO vdv_gtfs_tmp.stops VALUES (?,?,?,?);");
			stmt.setInt(1, id);
			stmt.setString(2, name);
			stmt.setDouble(3, lat);
			stmt.setDouble(4, longi);
			stmt.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void insertTrip(int routeID, int tripID){
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("INSERT INTO vdv_gtfs_tmp.trips VALUES (?,?);");
			stmt.setInt(1, routeID);
			stmt.setInt(2, tripID);
			stmt.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void insertStopTime(int tripID, int stopID, int seqNr){
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("INSERT INTO vdv_gtfs_tmp.stop_times VALUES (?,?,?);");
			stmt.setInt(1, tripID);
			stmt.setInt(2, stopID);
			stmt.setInt(3, seqNr);
			stmt.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<BusRoute> getBusRoute(){
		PreparedStatement stmt;
		BusRoute br = null;
		ArrayList<BusRoute> routes = null;
		try {
			stmt = conn.prepareStatement("SELECT * FROM vdv_gtfs_tmp.route_and_stop", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = stmt.executeQuery();
			if(rs.first()){
				routes = new ArrayList<BusRoute>();
				while(rs.next()){
					br = new BusRoute(rs.getInt(1), rs.getInt(2));
					routes.add(br);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return routes;
	}
	
	public void insertBusNode( double latitude, double longitude, int route){
		PreparedStatement stmt;
		BusRoute br = null;
		ArrayList<BusRoute> routes = null;
		try {
			stmt = conn.prepareStatement("INSERT INTO bz_isochrones_2014.bz_bus_nodes(node_mode, node_route_id, node_geometry)"
					+ " VALUES (?,?,ST_AsEWKT(ST_SetSRID(ST_Makepoint(?,?), ?)));");
			stmt.setInt(1, 0);
			stmt.setInt(2, route);
			stmt.setDouble(3, latitude);
			stmt.setDouble(4, longitude);
			stmt.setInt(5, 82344);
			stmt.execute();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public BusStop getStop(int id){
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("SELECT * FROM vdv_gtfs_tmp.stops WHERE stop_id = ?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			if(rs.first()) {
				return new BusStop(id, null, rs.getDouble(3), rs.getDouble(4));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
}
