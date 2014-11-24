package sasa_importer.street_network;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import sasa_importer.street_network.components.DenseNode;
import crosby.binary.Osmformat.DenseNodes;
import crosby.binary.Osmformat.HeaderBlock;
import crosby.binary.Osmformat.Node;
import crosby.binary.Osmformat.Relation;
import crosby.binary.Osmformat.Way;
import crosby.binary.BinaryParser;
import crosby.binary.file.BlockInputStream;
import crosby.binary.file.BlockReaderAdapter;

public class PBFParser extends BinaryParser{
	
	String file;
	ArrayList<DenseNode> allNodes;
	ArrayList<Way> allWays;
	
	public PBFParser(String file){
		this.file = file;
		allNodes = new ArrayList<DenseNode>();
		allWays = new ArrayList<Way>();
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
//            DenseInfo di = new DenseInfo(nodes.getDenseinfo().getVersion(i), nodes.getDenseinfo().getTimestamp(i), nodes.getDenseinfo().getChangeset(i), (long) parseLat(lastLat), (long) parseLon(lastLon));
//            DenseNode dn = new DenseNode(lastId, di, nodes.getKeysVals(i));
//            allNodes.add(dn);
            System.out.printf("Dense node, ID %d @ %.6f,%.6f\n", lastId, parseLat(lastLat), parseLon(lastLon));
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
            System.out.println("Way ID " + w.getId());
            StringBuilder sb = new StringBuilder();
            sb.append("  Nodes: ");
            long lastRef = 0;
            for (Long ref : w.getRefsList()) {
                lastRef+= ref;
                sb.append(lastRef).append(" ");
            }
            sb.append("\n  Key=value pairs: ");
            for (int i=0 ; i<w.getKeysCount() ; i++) {
                sb.append(getStringById(w.getKeys(i))).append("=")
                        .append(getStringById(w.getVals(i))).append(" ");
                
            }
            System.out.println(sb.toString());
        }
    }

    @Override
    protected void parse(HeaderBlock header) {
        System.out.println("Got header block.");
    }

    public void complete() {
        System.out.println("Complete!");
    }

}
