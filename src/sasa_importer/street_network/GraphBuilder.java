package sasa_importer.street_network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.postgis.LineString;
import org.postgis.MultiPoint;
import org.postgis.PGgeometry;
import org.postgis.Point;

import sasa_importer.database.DBConnector;
import sasa_importer.street_network.components.DenseNode;
import sasa_importer.street_network.components.Edge;
import sasa_importer.street_network.components.OSMWay;
import sasa_importer.street_network.components.RealNode;

public class GraphBuilder {
	
	private HashMap<Long, DenseNode> denseNodes;
	private HashMap<Long, OSMWay> ways;
	private HashMap<Long, List<Long>> crosses;
	private DBConnector db;
	
	public GraphBuilder(HashMap<Long, DenseNode> allNodes,  HashMap<Long, OSMWay> allWays, DBConnector conn){
		denseNodes = allNodes;
		ways = allWays;
		crosses = new HashMap<Long, List<Long>>();
		db = conn;
	}
	
	//find all crosses given the list of nodes of each way
    public void findCross(){
    	HashMap<Long, List<Long>> result = new HashMap<Long, List<Long>>();
    	Set<Long> keys = denseNodes.keySet();
    	for(OSMWay osmw : ways.values()){
    		List<Long> outer = osmw.getWayNodes();
    		for(Long l : outer){
    			if(keys.contains(l)){
	    			List<Long> nodes = result.get(l);
	    			if(nodes == null){
	    				nodes = new ArrayList<Long>();
	    				nodes.add(osmw.getId());
	    			}
	    			else
	    				nodes.add(osmw.getId());
	    			result.put(l, nodes);
    			}
    		}
    	}
    	crosses = result;
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
    
    public List<RealNode> getRealStreetNodes(){
//    	System.out.println("Nodes: " + nodes);
    	List<RealNode> result = new ArrayList<RealNode>();
    	for(Long l : denseNodes.keySet()){
    		if(l > 0 && String.valueOf(l).length() >= 7){
	    		DenseNode dn = denseNodes.get(l);
	    		Point p = new Point(dn.getdInfo().getLognitude(),dn.getdInfo().getLatitude());
	    		p.setSrid(4326);
	    		RealNode aNode = new RealNode(l, new PGgeometry(p));
	    		result.add(aNode);
//	    		System.out.println(aNode.toString());
    		}
    		else
    			continue;
    	}
    	System.out.println("Real nodes: " + result.size());
    	return result;
    }
    
    public HashMap<Long, RealNode> buildNodes(){
    	HashMap<Long, RealNode> realNodes = new HashMap<Long, RealNode>();
		List<RealNode> nodes = getRealStreetNodes();
		for(RealNode rn : nodes)
			realNodes.put(rn.getId(), rn);
		return realNodes;
	}
    
    public void insertNodes(Collection<RealNode> nodes){
    	boolean result = db.insertMultipleStreetNodes(nodes);
    	if(result)
    		System.out.println("Street nodes correctly inserted.");
    }
    
    public HashMap<Long, Edge> buildEdges(){
    	HashMap<Long, Edge> edges = new HashMap<Long, Edge>();
    	for(Long l : ways.keySet()){
    		OSMWay way = ways.get(l);
    		Edge e = new Edge(l, way.getWayNodes().get(0), way.getWayNodes().get(way.getWayNodes().size()-1), buildEdgeGeometry(way.getWayNodes()), 0);
//    		System.out.println("Geom: " + e.getGeometry().toString());
    		edges.put(l, e);
//    		edges.put(destination, reverseEdge(edgePoint, destination, source));
    	}
//    	Set<Long> crossesKeys = crosses.keySet();
//    	System.out.println("Crosses: " + crossesKeys.size());
//    	ArrayList<Point> edgePoint = new ArrayList<Point>();
//    	Long source = null; 
//    	Long destination = null;
//    	
//    	//creating an edge for each node that is a crosses
//    	for(Long cross : crossesKeys){
//    		source = cross;
//    		for(Long way : crosses.get(cross)){
//	    		OSMWay aWay = ways.get(way);
//				DenseNode aNode = denseNodes.get(cross);
//		    		
//		    		//search for the nodes forming the edge and the destination
//		    		for(Long node : aWay.getWayNodes()){
//		    			if(node != cross)
//		    				continue;
//		    			Point aPoint = new Point(aNode.getdInfo().getLognitude(), aNode.getdInfo().getLatitude());
//		    			edgePoint.add(aPoint);
//		    			if(crosses.get(node) == null){
//		    				aPoint = new Point(aNode.getdInfo().getLognitude(), aNode.getdInfo().getLatitude());
//		    				edgePoint.add(aPoint);
//		    			}
//		    			else{
//			    			aPoint = new Point(aNode.getdInfo().getLognitude(), aNode.getdInfo().getLatitude());
//		    				edgePoint.add(aPoint);
//		    				destination = node;
//		    				break;
//		    			}
//		    		}
//	    	}
//	    	PGgeometry edge = new PGgeometry(new LineString(edgePoint.toArray(new Point[]{})));
//	    	Edge e = new Edge(source, destination, edge, 0);
//	    	if(edges.get(source) == null){
//	    		edges.put(source, e);
//	    		edges.put(destination, reverseEdge(edgePoint, destination, source));
//	    	}
//    	}
    	return edges;
    }
    
    public Edge reverseEdge(ArrayList<Point> points, long id, long source, long destination){
    	Collections.reverse(points);
    	PGgeometry edge = new PGgeometry(new LineString(points.toArray(new Point[] {})));
    	return new Edge(id, source, destination, edge, 0);
    }
    
    public PGgeometry buildEdgeGeometry(List<Long> nodes){
    	ArrayList<Point> points = new ArrayList<Point>();
    	for(Long l : nodes){
    		DenseNode aNode = denseNodes.get(l);
    		if(aNode != null){
	    		Point p = new Point(aNode.getdInfo().getLognitude(), aNode.getdInfo().getLatitude());
	    		points.add(p);
    		}
    	}
    	return new PGgeometry(new MultiPoint(points.toArray(new Point[]{})));
    }
    
    public void insertEdges(Collection<Edge>edges){
    	boolean result = db.insertMultipleStreetEdges(edges);
    	if(result)
    		System.out.println("Street nodes correctly inserted.");
    }

}
