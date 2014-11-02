package sasa_importer;

import java.io.File;
import java.util.StringTokenizer;

import org.w3c.dom.NodeList;

import sasa_importer.street_network.XMLParser;


public class Main {
	
	public static void main(String[] args){
		System.gc();
		DBConnector db = null;
		switch (Integer.parseInt(args[0])){
		
		//Renaming the vdv file from *.x10 to *.X10
		case 1:
			System.out.println("File rename.");
			File vdv = new File("/var/lib/postgresql/bz_database/vdv/");
//			File vdv = new File("C:\\Users\\Luca\\Dropbox\\Uni\\Thesis\\Isochrones\\vdv\\");
			File[] files = vdv.listFiles();
			for(File f : files){
				StringTokenizer st = new StringTokenizer(f.getName(), ".");
				String name = st.nextToken();
				f.renameTo(new File("/var/lib/postgresql/bz_database/vdv/" + name + ".x10"));
//				f.renameTo(new File("C:\\Users\\Luca\\Dropbox\\Uni\\Thesis\\Isochrones\\vdv\\" + name + ".x10"));
			}
			break;
			
		//Populating the temporary database fro bus data	
		case 2:
			db = new DBConnector();
			db.emptyTmpDatabase();
			final BusDataParser bdp = new BusDataParser(db);
			Thread t1 = new Thread(new Runnable() {
				
				@Override
				public void run() {
					bdp.parseRoutes();
				}
			});
			
			Thread t2 = new Thread(new Runnable() {
				
				@Override
				public void run() {
					bdp.parseTrips();
				}
			});
			
			Thread t3 = new Thread(new Runnable() {
				
				@Override
				public void run() {
					bdp.parseCalendar();
					bdp.createCalendar();
				}
			});
			
			Thread t4 = new Thread(new Runnable() {
				
				@Override
				public void run() {
					bdp.parseStops();
				}
			});
			
			Thread t5 = new Thread(new Runnable() {
				
				@Override
				public void run() {
					bdp.parseTripSequence();
				}
			});
			t1.start();
			t2.start();
			t3.start();
			t4.start();
			t5.start();
			break;
			
		//Creating link network
		case 3:
			db = new DBConnector();
			final LinkNetwork ln = new LinkNetwork(db);
			Thread t6 = new Thread(new Runnable() {
				
				@Override
				public void run() {
					ln.createLinkNode();
					ln.updatePedestrianNetwork();
				}
			});
			
			t6.start();
			break;
			
		//Parsing Openstreetmap data for building the street network	
		case 4:
			XMLParser aParser = new XMLParser("C:\\Users\\Luca\\Dropbox\\Uni\\Bachelor\\Thesis\\Isochrones\\new schema\\street_network\\mebo_street.osm");
			aParser.separateSourceFile();
			aParser.readWayTag();
		}
	}

}
