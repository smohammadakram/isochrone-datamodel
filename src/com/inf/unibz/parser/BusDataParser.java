package com.inf.unibz.parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.print.attribute.HashAttributeSet;

import com.inf.unibz.database.DBConnector;
import com.inf.unibz.entity.BusRoute;
import com.inf.unibz.entity.BusStop;
import com.inf.unibz.entity.Route;
import com.inf.unibz.entity.Trip;
import com.inf.unibz.entity.TripConnection;

public class BusDataParser {
	
	private String file;
	private Hashtable<Integer, BusStop> stops;
	private ArrayList<Route> routes;
	private Hashtable<Integer, ArrayList<Trip>> trips;
	private Hashtable<Integer, ArrayList<TripConnection>> connections;
	private DBConnector db;
	private Hashtable<Integer, ArrayList<BusRoute>> stopCount;
	
	public BusDataParser(){
		db = new DBConnector("maps");
	}
	
	public void createStopsFromFile(){
		setFile("C:\\Users\\Luca\\Dropbox\\Uni\\Thesis\\SASA\\output\\output\\stops.txt");
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			br.readLine(); //next line skipped
			String line = br.readLine();
			StringTokenizer st = null;
			int id = -1;
			String name = null;
			double longi = 0.0;
			double lat = 0.0;
			BusStop bs = null;
			if(stops == null)
				stops = new Hashtable<Integer, BusStop>();
			while(line != null){
				st = new StringTokenizer(line, ",");
				id = Integer.parseInt(st.nextToken());
				name = st.nextToken();
				String s = st.nextToken() + "." + st.nextToken();
				String s1 = st.nextToken() + "." + st.nextToken();
				System.out.println(s);
				System.out.println(s1);
				lat = Double.parseDouble(s.substring(1, s.length()-2));
				longi = Double.parseDouble(s1.substring(1, s.length()-2));
//				bs = new BusStop(id, name, longi, lat);
//				stops.add(bs);
				db.insertStop(id, name, lat, longi);
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public double parseCoordinates(String s){
		StringTokenizer st = new StringTokenizer(s, ",");
		s = st.nextToken() + "." + st.nextToken() + st.nextToken();
		return Double.parseDouble(s);
	}
	
	public void setFile(String f){
		file = f;
	}
	
	public void parseRoutes(){
		setFile("C:\\Users\\Luca\\Dropbox\\Uni\\Thesis\\SASA\\output\\output\\routes.txt");
		BufferedReader br;
		Route r = null;
		try {
			br = new BufferedReader(new FileReader(file));
			br.readLine();
			String s = br.readLine();
			StringTokenizer st = new StringTokenizer(s, "\t");
			while(s != null){
				r = new Route(Integer.parseInt(st.nextToken()));
				routes.add(r);
				s = br.readLine();
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void parseTrips(){
		setFile("C:\\Users\\Luca\\Dropbox\\Uni\\Thesis\\SASA\\output\\output\\trips.txt");
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			ArrayList<Trip> all = new ArrayList<Trip>();
			br.readLine();
			String s = br.readLine();
			StringTokenizer st;
			Trip t = null;
			while(s != null){
				st = new StringTokenizer(s, ",");
				db.insertTrip(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
				s = br.readLine();
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public void parseTripSequence(){
		setFile("C:\\Users\\Luca\\Dropbox\\Uni\\Thesis\\SASA\\output\\output\\stop_times.txt");
		ArrayList<TripConnection> conns = new ArrayList<TripConnection>();
		BusStop bs = null;
		TripConnection tc = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			StringTokenizer st = null;
			br.readLine();
			String s = br.readLine();
			while(s != null){
				st = new StringTokenizer(s, ",");
				int t = Integer.parseInt(st.nextToken());
				int s1 = Integer.parseInt(st.nextToken());
				st.nextToken();
				st.nextToken();
				int seq = Integer.parseInt(st.nextToken());
				db.insertStopTime(t, s1, seq);
				s = br.readLine();
				conns.add(tc);
			} 
			br.close();
			populateConnections(conns);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void populateConnections(ArrayList<TripConnection> conns){
		if(connections == null)
			connections = new Hashtable<Integer, ArrayList<TripConnection>>();
		ArrayList<TripConnection> aux = null;
		for(TripConnection tc : conns){
			if(connections.get(tc.getTripID()) == null){
				aux = new ArrayList<TripConnection>();
				aux.add(tc);
				connections.put(tc.getTripID(), aux);
			}
			else
				connections.get(tc.getTripID()).add(tc);
		}
	}
	
	public void populateRouteTrips(ArrayList<Trip> ts){
		if(trips == null)
			trips = new Hashtable<Integer, ArrayList<Trip>>();
		ArrayList<Trip> aux = null;
		for(Trip t : ts){
			if(trips.get(t.getRoute()) == null){
				aux = new ArrayList<Trip>();
				aux.add(t);
				trips.put(t.getRoute(), aux);
			}
			else
				trips.get(t.getRoute()).add(t);
		}
	}
	
	public void groupStop(){
		ArrayList<BusRoute> routes = db.getBusRoute();
		ArrayList<BusRoute> stopRoutes = null;
		stopCount = new Hashtable<Integer, ArrayList<BusRoute>>();
		for(BusRoute br : routes){
			if(stopCount.get(br.getStop()) == null){
				stopRoutes = new ArrayList<BusRoute>();
				stopRoutes.add(br);
				stopCount.put(br.getStop(), stopRoutes);
			}
			else
				stopCount.get(br.getStop()).add(br);
		}
	}
	
	public void createBusNodes(){
		for(BusRoute br : db.getBusRoute()){
			BusStop bs = db.getStop(br.getStop());
			System.out.println(br.getStop());
			System.out.println(bs);
			db.insertBusNode(bs.getLatitude(), bs.getLongitude(), br.getRoute());
		}
	}
	
}
