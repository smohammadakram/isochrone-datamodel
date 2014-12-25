package sasa_importer.street_network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.postgis.LineString;
import org.postgis.PGgeometry;
import org.postgis.Point;

import sasa_importer.database.DBConnector;
import sasa_importer.street_network.components.DenseNode;
import sasa_importer.street_network.components.Edge;
import sasa_importer.street_network.components.RealNode;

public class GraphBuilder {
	
	private HashMap<Long, DenseNode> denseNodes;
	private HashMap<Long, RealNode> realNodes;
	private HashMap<Long, org.openstreetmap.osmosis.core.domain.v0_6.Way> ways;
	private String city;
	private DBConnector db;
	
	public GraphBuilder(HashMap<Long, DenseNode> allNodes,  HashMap<Long, org.openstreetmap.osmosis.core.domain.v0_6.Way> allWays, String city, DBConnector conn){
		denseNodes = allNodes;
		realNodes = new HashMap<Long, RealNode>();
		ways = allWays;
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
    	
    	System.out.println("Real nodes: " + realNodes.values().size());
    	Collection<RealNode> result = realNodes.values();
    	return result; 
    }
    
    public HashMap<Long, RealNode> buildNodes(){
    	System.out.println("Bulding real nodes.");
    	HashMap<Long, RealNode> realNodes = new HashMap<Long, RealNode>();
		Collection<RealNode> nodes = getRealStreetNodes();
		for(RealNode rn : nodes)
			realNodes.put(rn.getId(), rn);
		return realNodes;
	}
    
    public void insertNodes(Collection<RealNode> nodes){
    	System.out.println("Creating real nodes insertions script.");
    	db.insertMultipleStreetNodes(nodes, city);
		System.out.println("Street nodes correctly inserted.");
    }
    
    public HashMap<Long, Edge> buildEdges(){
    	System.out.println("Building edges.");
    	HashMap<Long, Edge> edges = new HashMap<Long, Edge>();
    	for(Long l : ways.keySet()){
    		org.openstreetmap.osmosis.core.domain.v0_6.Way way = ways.get(l);
    		Edge e = new Edge(l, way.getWayNodes().get(0).getNodeId(), (way.getWayNodes().get(way.getWayNodes().size()-1).getNodeId()), buildEdgeGeometry(way.getWayNodes()), 0);
    		edges.put(l, e);
//    		edges.put(destination, reverseEdge(edgePoint, destination, source));
    	}
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
    	System.out.println( "Creating edges population script.");
    	db.insertMultipleStreetEdges(edges);
		System.out.println("Street nodes correctly inserted.");
    }

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
    

}
