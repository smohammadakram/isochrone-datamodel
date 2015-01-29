package time_expanded_spatial_data.street_network;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openstreetmap.osmosis.core.domain.v0_6.CommonEntityData;
import org.openstreetmap.osmosis.core.domain.v0_6.OsmUser;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;

import time_expanded_spatial_data.street_network.components.DenseInfo;
import time_expanded_spatial_data.street_network.components.DenseNode;
import crosby.binary.Osmformat.DenseNodes;
import crosby.binary.Osmformat.HeaderBlock;
import crosby.binary.Osmformat.Node;
import crosby.binary.Osmformat.Relation;
import crosby.binary.Osmformat.Way;
import crosby.binary.BinaryParser;
import crosby.binary.Osmformat;
import crosby.binary.file.BlockInputStream;
import crosby.binary.file.BlockReaderAdapter;

public class PBFParser extends BinaryParser{
	
	String file;
	HashMap<Long, DenseNode> allNodes;
	HashMap<Long, org.openstreetmap.osmosis.core.domain.v0_6.Way> allWays;
	
	public PBFParser(String file){
		this.file = file;
		allNodes = new HashMap<Long, DenseNode>();
		allWays = new HashMap<Long, org.openstreetmap.osmosis.core.domain.v0_6.Way>();
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
	
	   /** Get the osmosis object representing a the user in a given Info protobuf.
     * @param info The info protobuf.
     * @return The OsmUser object */
    OsmUser getUser(Osmformat.Info info) {
        // System.out.println(info);
        if (info.hasUid() && info.hasUserSid()) {
            if (info.getUid() < 0) {
              return OsmUser.NONE;
            }
            return new OsmUser(info.getUid(), getStringById(info.getUserSid()));
        } else {
            return OsmUser.NONE;
        }
    }

    /** The magic number used to indicate no version number metadata for this entity. */
    static final int NOVERSION = -1;
    /** The magic number used to indicate no changeset metadata for this entity. */
    static final int NOCHANGESET = -1;
	

    @Override
    protected void parseRelations(List<Relation> rels) {
        if (!rels.isEmpty())
            System.out.println("[INFO] Got some relations to parse.");
//            for(Relation rel : rels)
//            	System.out.println("Relation, %e, ", rel.getId(), rel.get);
    }

    @Override
    protected void parseDense(DenseNodes nodes) {
    	System.out.println("[INFO] Parsing dense nodes.");
        long lastId=0;
        long lastLat=0;
        long lastLon=0;

        for (int i=0 ; i<nodes.getIdCount() ; i++) {
            lastId += nodes.getId(i);
            lastLat += nodes.getLat(i);
            lastLon += nodes.getLon(i);
            DenseInfo di = new DenseInfo(nodes.getDenseinfo().getVersion(i), nodes.getDenseinfo().getTimestamp(i), nodes.getDenseinfo().getChangeset(i), parseLat(lastLat), parseLon(lastLon));
            DenseNode dn = new DenseNode(lastId, di);
            allNodes.put(lastId, dn);
        }
    }

    @Override
    protected void parseNodes(List<Node> nodes) {
        for (Node n : nodes) {
            System.out.printf("[INFO] Regular node, ID %d @ %.6f,%.6f\n",
                    n.getId(),parseLat(n.getLat()),parseLon(n.getLon()));
        }
    }

    @Override
    protected void parseWays(List<Way> ways) {
    	System.out.println("[INFO] Parsing ways.");
    	boolean street = false;
    	
    	 for (Osmformat.Way i : ways) {
    		 street = false;
             List<Tag> tags = new ArrayList<Tag>();
             for (int j = 0; j < i.getKeysCount(); j++) {
            	 String tag = getStringById(i.getKeys(j));
            	 Tag t = new Tag(tag, getStringById(i.getVals(j)));
                 tags.add(t);
                 if(tag.equals("highway"))
                	 street = true;
             }
             
             //check if the current way is a street 
             if(!street)
            	 continue;
                 
             long lastId = 0;
             List<WayNode> nodes = new ArrayList<WayNode>();
             for (long j : i.getRefsList()) {
                 nodes.add(new WayNode(j + lastId));
                 lastId = j + lastId;
             }

             long id = i.getId();

             // long id, int version, Date timestamp, OsmUser user,
             // long changesetId, Collection<Tag> tags,
             // List<WayNode> wayNodes
             org.openstreetmap.osmosis.core.domain.v0_6.Way tmp;
             if (i.hasInfo()) {
                 Osmformat.Info info = i.getInfo();
                 tmp = new org.openstreetmap.osmosis.core.domain.v0_6.Way(new CommonEntityData(id, info.getVersion(), getDate(info),
                         getUser(info), info.getChangeset(), tags), nodes);
             } else {
            	 tmp = new org.openstreetmap.osmosis.core.domain.v0_6.Way(new CommonEntityData(id, NOVERSION, NODATE,
            			 OsmUser.NONE, NOCHANGESET, tags), nodes);

             }
             
             allWays.put(id, tmp);
   
        }
    }

    @Override
    protected void parse(HeaderBlock header) {
        System.out.println("[INFO] Got header block.");
    }

    public void complete() {
        System.out.println("[INFO] Complete!");
    }
    
    public HashMap<Long, DenseNode> getAllNodes(){
    	return allNodes;
    }
    
	public  HashMap<Long, org.openstreetmap.osmosis.core.domain.v0_6.Way> getAllWays(){
		return allWays;
	}
  
    
}
