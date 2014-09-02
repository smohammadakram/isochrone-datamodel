package com.inf.unibz.data_structure.adjacency;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import oracle.net.nt.ConnStrategy;

import com.inf.unibz.database.DBConnector;
import com.inf.unibz.entity.Edge;
import com.inf.unibz.entity.EdgeEntry;
import com.inf.unibz.entity.TripConnection;
import com.inf.unibz.entity.Vertex;

public class AdjacencyMatrix {
	
	public final static String MATRIX_FILE_NO_TIME = "C:\\Users\\Luca\\git\\Thesis\\docs\\matrix.csv";
	private int[] verteces;
	private DBConnector db;
	private Edge[][] matrix;
	private ArrayList<TripConnection> connections;
	private List<Edge> edges; 
	private List<Edge> busEdges;
	private Hashtable<Integer, ArrayList<TripConnection>> timetable;
	
	public AdjacencyMatrix(){
		db = new DBConnector("routing");
		verteces = db.getAllNodesAsArray();
		edges = db.getAllEdges(); 
	}
	
	public void setMatrixWithoutTime(){
		Vertex s, t = null;
		Edge e = null;
		if(matrix == null)
			matrix = new Edge[verteces.length][verteces.length];
		for(int i = 0; i < verteces.length; i++){
			s = new Vertex(verteces[i], "");
			System.out.println("Index: " + i);
			for(int j = 0; j < verteces.length; j++){
				t = new Vertex(verteces[j], "");
				
				//checking if an edge exists
				e = findEdge(s, t);	
				if(e != null && matrix[i][j] == null)
					matrix[i][j] = e;
			}
		}
		setTimetableMatrix();
	}
	
	public void setMatrixWithTime(){
		connections = db.getAllTripConnections();
		Vertex s, t = null;
		Edge e = null;
		ArrayList<TripConnection> conns = null;
		Edge me = null;
		if(matrix == null)
			matrix = new EdgeEntry[verteces.length][verteces.length];
		for(int i = 0; i < verteces.length; i++){
			s = new Vertex(verteces[i], "");
			for(int j = 0; j < verteces.length; j++){
				t = new Vertex(verteces[j], "");
				e = findEdge(s, t);
				if(e != null)
					if(e.getType() == 1){
						conns = findAllConnectionByEdge(e.getId());
						me = new EdgeEntry(e, conns);
					}
					else
						me = e;
				matrix[i][j] = me;
			}
		}
	}
	
	private void setTimetableMatrix(){
		busEdges = db.getBusEdges();
		if(timetable == null)
			timetable = new Hashtable<Integer, ArrayList<TripConnection>>();
		for(Edge e : busEdges){
			connections = db.getTripConnectionsByEdge(e.getId());
			if(connections != null)
				timetable.put(e.getId(), connections);
		}
	}
	
	public Edge[][] getMatrix(){
		return matrix;
	}
	
	public Edge findEdge(Vertex s, Vertex t){
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
	
	public Hashtable<Integer, ArrayList<TripConnection>> getTimetable(){
		return timetable;
	}

}