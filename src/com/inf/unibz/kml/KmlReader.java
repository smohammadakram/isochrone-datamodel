package com.inf.unibz.kml;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Geometry;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.Feature;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.Point;

import java.io.File;
import java.util.List;

import com.inf.unibz.entity.BusStop;

public class KmlReader {
	
	private String file;
	private Kml aKml;
	private ArrayList<BusStop> stops = new ArrayList<BusStop>();
	

	public KmlReader(){
		aKml = new Kml();
		file = "C:\\Users\\Luca\\Dropbox\\Uni\\Thesis\\SASA\\exported files\\sasa_ge_busdata.kml";
	}
	
	public int createStops(){
		aKml = Kml.unmarshal(new File(file));
		Document d = (Document) aKml.getFeature();
        System.out.println(d.getName());
        List<Feature> pms = d.getFeature();
        BusStop bs = null;
        for(Feature f: pms){
        	if(f instanceof Placemark){
                 Placemark placemark = (Placemark) f;
                 String stop = placemark.getName();
                 String[] nameSplit = stop.split(": ");
                 int id = Integer.parseInt(nameSplit[0]);
                 String name = nameSplit[1];
                 System.out.println(name);
                 Geometry geometry = placemark.getGeometry();
                 if(geometry != null) {
         	        if(geometry instanceof Point) {
         	        	Point p = (Point) geometry;
         	        	List<Coordinate> cs = p.getCoordinates();
         	        	bs = new BusStop(id, name, cs.get(0).getLongitude(), cs.get(0).getLatitude());
         	        	System.out.println(cs.get(0).getLongitude());
         	        	System.out.println(cs.get(0).getLatitude());
         	        	stops.add(bs);
         	        }
         	    }
                 System.out.println();
        	}
        }
        return stops.size();
	}
	
	public void updateValues(Connection c){
		try {
			PreparedStatement ps = null;
			for(BusStop bs: stops){
				ps = c.prepareStatement("UPDATE vdv.rec_ort SET ort_pos_laenge = ?, ort_pos_breite = ? WHERE ort_nr = ?;");
				ps.setInt(3, bs.getID());
				ps.setInt(1, bs.getCoordinateAsInt("longitude"));
				ps.setInt(2, bs.getCoordinateAsInt("latitude"));
				ps.execute();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}//EOF
