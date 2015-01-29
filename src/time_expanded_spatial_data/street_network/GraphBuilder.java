package time_expanded_spatial_data.street_network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;
import org.postgis.LineString;
import org.postgis.PGgeometry;
import org.postgis.Point;

import time_expanded_spatial_data.database.DBConnector;
import time_expanded_spatial_data.street_network.components.DenseNode;
import time_expanded_spatial_data.street_network.components.Edge;
import time_expanded_spatial_data.street_network.components.RealNode;

public class GraphBuilder {
	
	private HashMap<Long, DenseNode> denseNodes;
	private HashMap<Long, RealNode> realNodes;
	private HashMap<Long, Way> ways;
	private String city;
	private DBConnector db;
	
	public GraphBuilder(HashMap<Long, DenseNode> allNodes,  HashMap<Long, org.openstreetmap.osmosis.core.domain.v0_6.Way> allWays, String city, DBConnector conn){
		denseNodes = allNodes;
		realNodes = new HashMap<Long, RealNode>();
		ways = allWays;
		this.city = city;
		db = conn;
	}
    
  //checks if two way intersect by finding the same node in the lists of nodes describing the ways
    public long intersect(List<Long> outer, List<Long> inner){
    	for(Long l : outer)
    		if(l > 0 && String.valueOf(l).length() >= 7)
    			for(Long l1: inner)
	    			if(l1 > 0 && String.valueOf(l1).length() >= 7 && l1.equals(l))
	    				return l1;
    	return -1;
    }
    
    /**
     * 
     * @return The real node representing crosses in the street network. The nodes between each couple of source-destination node are discarded in this phase.
     */
    public Collection<RealNode> getRealStreetNodes(){
//    	System.out.println("Nodes: " + nodes);
    	for(org.openstreetmap.osmosis.core.domain.v0_6.Way way : ways.values()){
    		//getting source and destination of the way, which are real nodes
    		Long source = way.getWayNodes().get(0).getNodeId();
    		Long destination = way.getWayNodes().get(way.getWayNodes().size()-1).getNodeId();
    		
    		//add the source to the list if it is not there yet
    		if(realNodes.get(source) == null){
	    		DenseNode dn = denseNodes.get(source);
	    		Point p = new Point(dn.getdInfo().getLognitude(),dn.getdInfo().getLatitude());
	    		p.setSrid(4326);
	    		RealNode aNode = new RealNode(source, new PGgeometry(p));
	    		realNodes.put(source, aNode);
    		}
    		
    		//same for the destination
    		if(realNodes.get(destination) == null){
    			DenseNode dn = denseNodes.get(destination);
	    		Point p = new Point(dn.getdInfo().getLognitude(),dn.getdInfo().getLatitude());
	    		p.setSrid(4326);
	    		RealNode aNode = new RealNode(destination, new PGgeometry(p));
	    		realNodes.put(destination, aNode);
    		}
    	}
    	
    	System.out.println("[INFO] Real nodes: " + realNodes.values().size());
    	Collection<RealNode> result = realNodes.values();
    	return result; 
    }
    
    public HashMap<Long, RealNode> buildNodes(){
    	System.out.println("[INFO] Bulding real nodes.");
    	HashMap<Long, RealNode> realNodes = new HashMap<Long, RealNode>();
		Collection<RealNode> nodes = getRealStreetNodes();
		db.openWriter(city + "_street_nodes_import.sql");
		db.deleteClause(city + "_street_nodes_import");
		db.resetCheckpoint();
		int counter = 0;
		for(RealNode rn : nodes){
			if(counter == 999){
				insertNodes(nodes);
				realNodes = new HashMap<Long, RealNode>();
			}
			realNodes.put(rn.getId(), rn);
			counter++;
		}
		db.closeWriter();
		return realNodes;
	}
    
    public void insertNodes(Collection<RealNode> nodes){
    	db.insertMultipleStreetNodes(nodes, city);
    }
    
    public ArrayList<Edge> buildEdges(){
    	System.out.println("[INFO] Building edges.");
//    	HashMap<Long, Edge> edges = new HashMap<Long, Edge>();
    	ArrayList<Edge> edges = new ArrayList<Edge>();
    	db.openWriter(city + "_street_edges_import.sql");
    	db.deleteClause(city + "_street_edges_import");
    	db.resetCheckpoint();
    	int counter = 0;
    	for(Long l : ways.keySet()){
    		if(counter == 999){
    			insertEdges(edges);
    			edges = new ArrayList<Edge>();
    		}
    		org.openstreetmap.osmosis.core.domain.v0_6.Way way = ways.get(l);
    		List<WayNode> tmp = way.getWayNodes();
    		Edge e = new Edge(l, tmp.get(0).getNodeId(), (tmp.get(way.getWayNodes().size()-1).getNodeId()), buildEdgeGeometry(tmp), 0);
    		edges.add(e);
    		Collections.reverse(tmp);
    		e = new Edge(l, tmp.get(0).getNodeId(), (tmp.get(way.getWayNodes().size()-1).getNodeId()), buildEdgeGeometry(tmp), 0);
    		edges.add(e);
//    		edges.put(destination, reverseEdge(edgePoint, destination, source));
    		counter += 2;
    	}
    	db.closeWriter();
    	return edges;
    }
    
    public Edge reverseEdge(ArrayList<Point> points, long id, long source, long destination){
    	Collections.reverse(points);
    	PGgeometry edge = new PGgeometry(new LineString(points.toArray(new Point[] {})));
    	return new Edge(id, source, destination, edge, 0);
    }
    
    public PGgeometry buildEdgeGeometry(List<org.openstreetmap.osmosis.core.domain.v0_6.WayNode> nodes){
    	ArrayList<Point> points = new ArrayList<Point>();
    	for(org.openstreetmap.osmosis.core.domain.v0_6.WayNode wn : nodes){
    		DenseNode aNode = denseNodes.get(wn.getNodeId());
    		if(aNode != null){
	    		Point p = new Point(aNode.getdInfo().getLognitude(), aNode.getdInfo().getLatitude());
	    		points.add(p);
    		}
    	}
    	return new PGgeometry(new LineString(points.toArray(new Point[]{})));
    }
    
    public void insertEdges(Collection<Edge>edges){
    	db.insertMultipleStreetEdges(edges, city);
    }

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

}
