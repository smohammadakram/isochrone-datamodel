package sasa_importer.street_network;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;

import crosby.binary.Fileformat.Blob;
import crosby.binary.Fileformat.BlobHeader;
import crosby.binary.Osmformat.HeaderBlock;
import crosby.binary.Osmformat.PrimitiveBlock;

public class PBFParser {
	
	public void parsePBF(){
		try {
			FileInputStream fis = new FileInputStream("us-pacific.osm.pbf");
			DataInputStream dis = new DataInputStream(fis);
			
			for (;;) {
				if (dis.available() <= 0) break;
				
				int len = dis.readInt();
				byte[] blobHeader = new byte[len];
				dis.read(blobHeader);
				BlobHeader h = BlobHeader.parseFrom(blobHeader);
				byte[] blob = new byte[h.getDatasize()];
				dis.read(blob);
				Blob b = Blob.parseFrom(blob);

				InputStream blobData;
				if (b.hasZlibData()) {
					blobData = new InflaterInputStream(b.getZlibData().newInput());
				} else {
					blobData = b.getRaw().newInput();
				}
				System.out.println("> " + h.getType());
				if (h.getType().equals("OSMHeader")) {
					HeaderBlock hb = HeaderBlock.parseFrom(blobData);
					System.out.println("hb: " + hb.getSource());
				} else if (h.getType().equals("OSMData")) {
					PrimitiveBlock pb = PrimitiveBlock.parseFrom(blobData);
					System.out.println("pb: " + pb.getGranularity());
				}
			}
			
			fis.close();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
