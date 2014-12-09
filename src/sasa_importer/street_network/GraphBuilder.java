package sasa_importer.street_network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.postgis.PGgeometry;
import org.postgis.Point;

import sasa_importer.street_network.components.DenseNode;
import sasa_importer.street_network.components.Edge;
import sasa_importer.street_network.components.OSMWay;
import sasa_importer.street_network.components.RealNode;

public class GraphBuilder {
	
	private HashMap<Long, DenseNode> denseNodes;
	private HashMap<Long, OSMWay> ways;
	private HashMap<Long, List<Long>> crosses;
	
	public GraphBuilder(HashMap<Long, DenseNode> allNodes,  HashMap<Long, OSMWay> allWays){
		denseNodes = allNodes;
		ways = allWays;
		crosses = new HashMap<Long, List<Long>>();
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
	    		Point p = new Point(dn.getdInfo().getLognitude(), dn.getdInfo().getLatitude());
	    		p.setSrid(4326);
	    		RealNode aNode = new RealNode(l, new PGgeometry(p));
	    		result.add(aNode);
	    		System.out.println(aNode.toString());
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
    
    public HashMap<Long, Edge> buildEdges(){
    	HashMap<Long, Edge> edges = new HashMap<Long, Edge>();
    	Set<Long> crossesKeys = crosses.keySet();
    	for(Long cross : crossesKeys){
    		//TODO generate edges
    	}
    	return edges;
    }

}
