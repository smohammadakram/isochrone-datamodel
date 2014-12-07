package sasa_importer.street_network;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import sasa_importer.street_network.components.DenseInfo;
import sasa_importer.street_network.components.DenseNode;
import sasa_importer.street_network.components.OSMWay;
import crosby.binary.Osmformat.DenseNodes;
import crosby.binary.Osmformat.HeaderBlock;
import crosby.binary.Osmformat.Info;
import crosby.binary.Osmformat.Node;
import crosby.binary.Osmformat.Relation;
import crosby.binary.Osmformat.Way;
import crosby.binary.BinaryParser;
import crosby.binary.file.BlockInputStream;
import crosby.binary.file.BlockReaderAdapter;

public class PBFParser extends BinaryParser{
	
	String file;
	HashMap<Long, DenseNode> allNodes;
	HashMap<Long, OSMWay> allWays;
	
	public PBFParser(String file){
		this.file = file;
		allNodes = new HashMap<Long, DenseNode>();
		allWays = new HashMap<Long, OSMWay>();
	}
	
	public void parsePBF(){
		InputStream input;
		try {
			input = new FileInputStream(file);
			BlockReaderAdapter brad = this;
			new BlockInputStream(input, brad).process();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

    @Override
    protected void parseRelations(List<Relation> rels) {
        if (!rels.isEmpty())
            System.out.println("Got some relations to parse.");
//            for(Relation rel : rels)
//            	System.out.println("Relation, %e, ", rel.getId(), rel.get);
    }

    @Override
    protected void parseDense(DenseNodes nodes) {
        long lastId=0;
        long lastLat=0;
        long lastLon=0;

        for (int i=0 ; i<nodes.getIdCount() ; i++) {
            lastId += nodes.getId(i);
            lastLat += nodes.getLat(i);
            lastLon += nodes.getLon(i);
            DenseInfo di = new DenseInfo(nodes.getDenseinfo().getVersion(i), nodes.getDenseinfo().getTimestamp(i), nodes.getDenseinfo().getChangeset(i), (long) parseLat(lastLat), (long) parseLon(lastLon));
            DenseNode dn = new DenseNode(lastId, di);
            allNodes.put(lastId, dn);
//            System.out.printf("Dense node, ID %d @ %.6f,%.6f\n", lastId, parseLat(lastLat), parseLon(lastLon));
        }
    }

    @Override
    protected void parseNodes(List<Node> nodes) {
        for (Node n : nodes) {
            System.out.printf("Regular node, ID %d @ %.6f,%.6f\n",
                    n.getId(),parseLat(n.getLat()),parseLon(n.getLon()));
        }
    }

    @Override
    protected void parseWays(List<Way> ways) {
        for (Way w : ways) {
//    	for(int i = 0; i < ways.size(); i++){
//    		Way w = ways.get(i);
        	Info in = w.getInfo();
        	OSMWay aWay = new sasa_importer.street_network.components.OSMWay(in.getVersion(),
        			w.getId(), in.getTimestamp(), in.getChangeset(), w.getRefsList());
//        	System.out.println(aWay.getWayNodes().get(0));
        	allWays.put(w.getId(), aWay);
//            System.out.println("Way ID " + w.getId());
//            StringBuilder sb = new StringBuilder();
//            sb.append("  Nodes: ");
//            long lastRef = 0;
//            for (Long ref : w.getRefsList()) {
//                lastRef+= ref;
//                sb.append(lastRef).append(" ");
//            }
//            sb.append("\n  Key=value pairs: ");
//            for (int i=0 ; i<w.getKeysCount() ; i++) {
//                sb.append(getStringById(w.getKeys(i))).append("=")
//                        .append(getStringById(w.getVals(i))).append(" ");
                
//            }
//            System.out.println(sb.toString());
        }
    }

    @Override
    protected void parse(HeaderBlock header) {
        System.out.println("Got header block.");
    }

    public void complete() {
        System.out.println("Complete!");
    }
    
    public HashMap<Long, DenseNode> getAllNodes(){
    	return allNodes;
    }
    
	public  HashMap<Long, OSMWay> getAllWays(){
		return allWays;
	}
  
    
}
