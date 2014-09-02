package com.inf.unibz.data_structure.adjacency;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.inf.unibz.database.DBConnector;
import com.inf.unibz.entity.Edge;
import com.inf.unibz.entity.EdgeEntry;
import com.inf.unibz.entity.TripConnection;
import com.inf.unibz.entity.Vertex;
import com.inf.unibz.entity.VertexEntry;

public class AdjacencyList {
	
	private List<Edge> edges; 
	private DBConnector db;
	private int[] verteces;
	private ArrayList<Vertex> busVerteces;
	private ArrayList<TripConnection> connections;
	private Hashtable<Integer, ArrayList<TripConnection>> timetable;
	
	/**
	 * Adjacent table with time schedule.
	 */
	private Hashtable<Integer, ArrayList<Edge>> list;
	
	public AdjacencyList(){
		db = new DBConnector("routing");
		list = new Hashtable<Integer, ArrayList<Edge>>();
		edges = db.getAllEdges();
		verteces = db.getAllNodesAsArray();
	}
	
	public void setListWithoutTime(){
		Vertex s = null;
		Vertex t = null;
		Edge e = null;
		ArrayList<Edge> neighbours = null;
		for(int i = 0; i < verteces.length; i++){
			s = new Vertex(verteces[i], "");
			System.out.println("Index list " + i);
			neighbours = new ArrayList<Edge>();
			for(int j = 0; j < verteces.length; j++){
				t = new Vertex(verteces[j], "");
				e = findEdge(edges, s, t);
				if(e != null){
					neighbours.add(e);
				}
			}
			list.put(s.getId(), neighbours);
		}
		setTimetableList();
	}
	
	public  void setListWithTime(){
		connections = db.getAllTripConnections();
		Vertex s = null;
		Vertex t = null;
		Edge e, ee = null;
		ArrayList<Edge> neighbours = null; 
		ArrayList<TripConnection> conns = null;
		for(int i = 0; i < verteces.length; i++){
			s = new Vertex(verteces[i], "");
			neighbours = new ArrayList<Edge>();
			for(int j = 0; j < verteces.length; j++){
				t = new Vertex(verteces[j], "");
				e = findEdge(edges, s, t);
				
				if(e != null){
					if(e.getType() == 1){
						conns = findAllConnectionByEdge(e.getId());
						ee = new EdgeEntry(e, conns);
					}
					neighbours.add(ee);
				}
			}
			list.put(s.getId(), neighbours);
		}
	}
	
	private void setTimetableList(){
		busVerteces = db.getBusNodes();
		connections = db.getAllTripConnections(); 
		ArrayList<TripConnection> conns = null;
		if(timetable == null)
			timetable = new Hashtable<Integer, ArrayList<TripConnection>>();
		for(Vertex v : busVerteces){
			conns = new ArrayList<TripConnection>();
			for(TripConnection tc : connections){
				if(tc.getSourceID() == v.getId())
					conns.add(tc);
			}
			timetable.put(v.getId(), conns);
		}
	}
	
	public int[] getEdgeExtreme(int id){
		int[] extremes = new int[2];
		Edge e = edges.get(id);
		extremes[0]	= e.getSource().getId();
		extremes[1] = e.getDestination().getId();
		return extremes;
	}
	
	public Hashtable<Integer, ArrayList<Edge>> getListWithoutTime(){
		return list;
	}
	
	public Edge findEdge(List<Edge> edges, Vertex s, Vertex t){
		for(Edge e: edges)
			if(e.getSource().getId() == s.getId() && e.getDestination().getId() == t.getId())
				return e;
		return null;
	}
	
	public ArrayList<TripConnection> findAllConnectionByEdge(int edgeID){
		ArrayList<TripConnection> res = new ArrayList<TripConnection>();
		for(TripConnection tc : connections)
			if(tc.getEdgeID() == edgeID)
				res.add(tc);
		return res;
	}
	
	
}