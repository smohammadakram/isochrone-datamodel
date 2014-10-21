package sasa_importer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.StringTokenizer;

public class BusDataParser {
	
	private String file;
	private DBConnector db;
	
	public BusDataParser(DBConnector conn){
		db = conn;
	}
	
	public void parseStops(){
//		setFile("C:\\Users\\Luca\\Dropbox\\Uni\\Thesis\\SASA\\output\\output\\stops.txt");
		try {
//			BufferedReader br = new BufferedReader(new FileReader(file));
			BufferedReader br = new BufferedReader(new FileReader("/var/lib/postgresql/gtfs/stops.txt"));
//			BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Luca\\Dropbox\\Uni\\Thesis\\Isochrones\\output\\stops.txt"));
			br.readLine(); //next line skipped
			String line = br.readLine();
			StringTokenizer st = null;
			int id = -1;
			String name = null;
			double longi = 0.0;
			double lat = 0.0;
			String script = "";
			script =  "INSERT INTO vdv_gtfs_tmp.stops VALUES\n"; 
			while(line != null){
				st = new StringTokenizer(line, ",");
				id = Integer.parseInt(st.nextToken());
				name = st.nextToken();
				String s = st.nextToken() + "." + st.nextToken();
				String s1 = st.nextToken() + "." + st.nextToken();
				lat = Double.parseDouble(s.substring(1, s.length()-2));
				longi = Double.parseDouble(s1.substring(1, s.length()-2));
//				bs = new BusStop(id, name, longi, lat);
//				stops.add(bs);
				if(name.contains("\'"))
					name.replaceAll("\'", "\\'");
				script += "('" + id + "', '" + name + "', '" + lat + "', '" + longi + "')";
				System.out.println(db.insertStop(id, name, lat, longi));
				line = br.readLine();
//				if(line != null)
//					script += ",\n";
			}
			script += ";";
			System.out.println(script);
			Statement stmt;
			try {
				stmt = DBConnector.getConnection().createStatement();
				stmt.execute(script);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public double parseCoordinates(String s){
		StringTokenizer st = new StringTokenizer(s, ",");
		s = st.nextToken() + "." + st.nextToken() + st.nextToken();
		return Double.parseDouble(s);
	}
	
	public void setFile(String f){
		file = f;
	}
	
	public String getFile(){
		return file;
	}
	
	public void parseRoutes(){
//		setFile("C:\\Users\\Luca\\Dropbox\\Uni\\Thesis\\SASA\\output\\output\\routes.txt");
		BufferedReader br;
		try {
//			br = new BufferedReader(new FileReader(file));
			br = new BufferedReader(new FileReader("/var/lib/postgresql/bz_database/gtfs/routes.txt"));
//			br = new BufferedReader(new FileReader("C:\\Users\\Luca\\Dropbox\\Un1i\\Thesis\\Isochrones\\output\\routes.txt"));
			br.readLine();
			String s = br.readLine();
			StringTokenizer st = new StringTokenizer(s, ",");
			String script = "";
			script =  "INSERT INTO vdv_gtfs_tmp.routes VALUES\n"; 
			while(s != null){
				st = new StringTokenizer(s, ",");
				st.nextToken();
				script += "('" + Integer.parseInt(st.nextToken()) + "', '" + st.nextToken() + "', '" + st.nextToken() + "')";
//				db.insertRoute(Integer.parseInt(st.nextToken()), st.nextToken(), st.nextToken());
				s = br.readLine();
				if(s != null)
					script += ",\n";
			}
			script += ";";
			System.out.println(script);
			Statement stmt = DBConnector.getConnection().createStatement();
			stmt.execute(script);
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void parseTrips(){
//		setFile("C:\\Users\\Luca\\Dropbox\\Uni\\Thesis\\SASA\\output\\output\\trips.txt");
		try {
//			BufferedReader br = new BufferedReader(new FileReader(file));
			BufferedReader br = new BufferedReader(new FileReader("/var/lib/postgresql/bz_database/gtfs/trips.txt"));
//			BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Luca\\Dropbox\\Uni\\Thesis\\Isochrones\\output\\trips.txt"));
			br.readLine();
			String s = br.readLine();
			StringTokenizer st;
			String script = "";
			script =  "INSERT INTO vdv_gtfs_tmp.trips VALUES\n"; 
			while(s != null){
				st = new StringTokenizer(s, ",");
				script += "('" + Integer.parseInt(st.nextToken()) + "', '" + Integer.parseInt(st.nextToken()) + "', '" + Integer.parseInt(st.nextToken()) + "')";
//				db.insertTrip(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
				s = br.readLine();
				if(s != null)
					script += ",\n";
			}
			script += ";";
			System.out.println(script);
			Statement stmt = DBConnector.getConnection().createStatement();
			stmt.execute(script);
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
	public void parseTripSequence(){
//		setFile("C:\\Users\\Luca\\Dropbox\\Uni\\Thesis\\SASA\\output\\output\\stop_times.txt");
		try {
//			BufferedReader br = new BufferedReader(new FileReader(file));
			BufferedReader br = new BufferedReader(new FileReader("/var/lib/postgresql/bz_database/gtfs/stop_times.txt"));
//			BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Luca\\Dropbox\\Uni\\Thesis\\Isochrones\\output\\stop_times.txt"));
			StringTokenizer st = null;
			br.readLine();
			String s = br.readLine();
			String script = "";
			script =  "INSERT INTO vdv_gtfs_tmp.stop_times VALUES\n"; 
			while(s != null){
				st = new StringTokenizer(s, ",");
				script += "('" + Integer.parseInt(st.nextToken()) + "', '" + Integer.parseInt(st.nextToken()) + "', '" + st.nextToken() + "', '" + st.nextToken() + "', '" + Integer.parseInt(st.nextToken()) + "')";
//				System.out.println(db.insertStopTime(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), st.nextToken(), st.nextToken(), Integer.parseInt(st.nextToken())));
				s = br.readLine();
				if(s != null)
					script += ",\n";
			}
			script += ";";
			System.out.println(script);
			Statement stmt = DBConnector.getConnection().createStatement();
			stmt.execute(script); 
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void parseCalendar(){
//		setFile("C:\\Users\\Luca\\Dropbox\\Uni\\Thesis\\SASA\\output\\output\\calendar.txt");
		try {
//			BufferedReader br = new BufferedReader(new FileReader(file));
			BufferedReader br = new BufferedReader(new FileReader("/var/lib/postgresql/bz_database/gtfs/calendar.txt"));
//			BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Luca\\Dropbox\\Uni\\Thesis\\Isochrones\\output\\calendar.txt"));
			StringTokenizer st = null;
			br.readLine();
			String s = br.readLine();
			String script = "";
			script =  "INSERT INTO vdv_gtfs_tmp.calendar(service_id,"
					+ "monday,"
					+ "tuesday,"
					+ "wednesday,"
					+ "thursday,"
					+ "friday,"
					+ "saturday,"
					+ "sunday,"
					+ "start_date,"
					+ "end_date) VALUES\n"; 
			while(s != null){
				st = new StringTokenizer(s, ",");
				script += "('" + Integer.parseInt(st.nextToken()) + 
						"', '" + isValidDay(st.nextToken()) + 
						"', '" + isValidDay(st.nextToken()) + 
						"', '" + isValidDay(st.nextToken()) +
						"', '" + isValidDay(st.nextToken()) +
						"', '" + isValidDay(st.nextToken()) +
						"', '" + isValidDay(st.nextToken()) +
						"', '" + isValidDay(st.nextToken()) + 
						"', '" + st.nextToken() + "', '" + st.nextToken() + "'),\n";
//				db.insertCalendar(Integer.parseInt(st.nextToken()), new boolean[] {isValidDay(st.nextToken()), 
//					isValidDay(st.nextToken()), 
//					isValidDay(st.nextToken()), 
//					isValidDay(st.nextToken()), 
//					isValidDay(st.nextToken()), 
//					isValidDay(st.nextToken()), 
//					isValidDay(st.nextToken())}, st.nextToken(), st.nextToken());
				s = br.readLine();
			} 
			br.close();
			
//			setFile("C:\\Users\\Luca\\Dropbox\\Uni\\Thesis\\SASA\\output\\output\\calendar_dates.txt");
//			br = new BufferedReader(new FileReader(file));
			br = new BufferedReader(new FileReader("/var/lib/postgresql/bz_database/gtfs/calendar_dates.txt"));
//			br = new BufferedReader(new FileReader("C:\\Users\\Luca\\Dropbox\\Uni\\Thesis\\Isochrones\\output\\calendar_dates.txt"));
			br.readLine();
			s = br.readLine();
			Calendar c = null;
			while(s != null){
				st = new StringTokenizer(s, ",");
				int id = Integer.parseInt(st.nextToken());
				System.out.println(id);
				String date = st.nextToken();
				System.out.println(date);
				c = parseDateToCalendar(date);
				boolean[] validity = {false, false, false, false, false, false, false};
				validity[c.get(Calendar.DAY_OF_WEEK)-1] = true;
				script += "('" + id + 
						"', '" + validity[0] + 
						"', '" + validity[1] + 
						"', '" + validity[2] +
						"', '" + validity[3] +
						"', '" + validity[4] +
						"', '" + validity[5] +
						"', '" + validity[6] + 
						"', '" + date + "', '" + date + "')";
//				db.insertCalendar(id, validity, date, date);
				s = br.readLine();
				if(s != null)
					script += ",\n";
			}
			script += ";";
			System.out.println(script);
			Statement stmt = DBConnector.getConnection().createStatement();
			stmt.execute(script); 
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isValidDay(String valid){
		if(valid.equals("0"))
			return false;
		else
			return true;
	}
	
	public String parseDate(String d){
		return d.substring(0, 4) + "-" + d.substring(4, 6) + "-" + d.substring(6, 8); 
	}
	
	public long parseDateToMillis(String d){
		System.out.println(d);
		Calendar c = new GregorianCalendar(Integer.parseInt(d.substring(0, 4)), Integer.parseInt(d.substring(4, 6)), Integer.parseInt(d.substring(6, 8)));
		return c.getTimeInMillis();
	}
	
	public Calendar parseDateToCalendar(String d){
		Calendar c = new GregorianCalendar(Integer.parseInt(d.substring(0, 4)), Integer.parseInt(d.substring(4, 6)), Integer.parseInt(d.substring(6, 8)));
		return c;
	}
	
	public void createCalendar(){
		List<Service> services = db.getCalendars();
		Calendar start = null;
		Calendar end = null;
		int doyStart, doyEnd, dow = -1;
		int[] validityVector = null;
		boolean[] validity = null;
		String dbVector = null;
		for(Service s : services){
			start = new GregorianCalendar();
			end = new GregorianCalendar();
			start.setFirstDayOfWeek(Calendar.MONDAY);
			end.setFirstDayOfWeek(Calendar.MONDAY);
			validityVector = new int[366];
			dbVector = new String();
			for(int i = 0; i < 366; i++)
				validityVector[i] = 0;
			start.setTimeInMillis(parseDateToMillis(s.getStartDate()));
			end .setTimeInMillis(parseDateToMillis(s.getEndDate()));
			doyStart = start.get(Calendar.DAY_OF_YEAR);
			doyEnd = end.get(Calendar.DAY_OF_YEAR);
			validity = s.getValidity();
			Calendar c = (Calendar) start.clone();
			while(doyStart <= doyEnd){
				dow = c.get(Calendar.DAY_OF_WEEK);
				if(validity[dow-1])
					validityVector[doyStart-1] = 1;
				c.add(Calendar.DAY_OF_YEAR, +1);
				doyStart = c.get(Calendar.DAY_OF_YEAR);
			}
			for(int i = 0; i < 366; i++)
				dbVector += validityVector[i];
//			System.out.println(dbVector);
			db.insertService(s.getId(), s.getStartDate(), s.getEndDate(), dbVector);
		}
	}
	
}
