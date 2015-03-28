package datamodel.timeexpanded.busnetwork;

import datamodel.database.DBConnector;

import java.io.BufferedReader;
import java.io.File;
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
//	static String GTFS = "gtfs/";
	private String gtfs;
	private String city;

	public BusDataParser(final DBConnector conn, final String gtfs, final String city) {
		db = conn;
		this.gtfs = gtfs + "/";
		this.city = city;
	}

	public void parseStops() {
		try {
//			System.out.println("[INFO] Parsing bus stops...");
			final BufferedReader br = new BufferedReader(new FileReader(new File(gtfs + "stops.txt")));
			br.readLine(); //next line skipped
			String line = br.readLine();
			StringTokenizer st = null;
			int id = -1;
			String name = null;
			double longi = 0.0;
			double lat = 0.0;
			String script = "";
			script = "DELETE FROM vdv_gtfs_tmp.stops CASCADE;";
			Statement stmt = DBConnector.getConnection().createStatement();
			stmt.execute(script);
//			final boolean comma = false;
			while (line != null) {
				script = "INSERT INTO vdv_gtfs_tmp.stops VALUES\n";
				String aux = "";
//				if(comma)
//					aux += ",\n";
//				comma = true;
				st = new StringTokenizer(line, ",");
				id = Integer.parseInt(st.nextToken());
				name = st.nextToken();
				final String s = st.nextToken() + "." + st.nextToken();
				final String s1 = st.nextToken() + "." + st.nextToken();
				lat = Double.parseDouble(s.substring(1, s.length() - 2));
				longi = Double.parseDouble(s1.substring(1, s.length() - 2));
//				bs = new BusStop(id, name, longi, lat);
//				stops.add(bs);
//				if(name.contains("\'"))
//					name.replaceAll("\'", "\\'");
				name = name.replace("\'", " ");
				aux += "(" + id + ", '" + name + "', " + lat + ", " + longi + ")";
//				db.insertStop(id, name, lat, longi);
				line = br.readLine();
//				if(line != null)
//					script += ",\n";
				script += aux;
				script += ";";
//				System.out.println(script);
				stmt = DBConnector.getConnection().createStatement();
				stmt.execute(script);
			}

//			System.out.println(script);
			br.close();
		} catch (final IOException e) {
			e.printStackTrace();
		} catch (final SQLException e1) {
			e1.printStackTrace();
		}
//		System.out.println("[INFO] Parsing bus stops...Done.");
	}

	public double parseCoordinates(String s) {
//		System.out.println("[INFO] Parsing coordinates...");
		final StringTokenizer st = new StringTokenizer(s, ",");
		s = st.nextToken() + "." + st.nextToken() + st.nextToken();
//		System.out.println("[INFO] Parsing coordinates...Done.");
		return Double.parseDouble(s);
	}

	public void setFile(final String f) {
		file = f;
	}

	public String getFile() {
		return file;
	}

	public void parseRoutes() {
//		System.out.println("[INFO] Parsing routes...");
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(new File(gtfs + "routes.txt")));

			br.readLine();
			String s = br.readLine();
			StringTokenizer st = new StringTokenizer(s, ",");
			String script = "";
			script = "INSERT INTO vdv_gtfs_tmp.routes VALUES\n";
			while (s != null) {
				st = new StringTokenizer(s, ",");
				st.nextToken();
				script += "('" + Integer.parseInt(st.nextToken()) + "', '" + st.nextToken() + "', '" + st.nextToken() + "')";
//				db.insertRoute(Integer.parseInt(st.nextToken()), st.nextToken(), st.nextToken());
				s = br.readLine();
				if (s != null) {
					script += ",\n";
				}
			}
			script += ";";
//			System.out.println(script);
			final Statement stmt = DBConnector.getConnection().createStatement();
			stmt.execute(script);
			br.close();
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		//		System.out.println("[INFO] Parsing routes...Done.");
	}

	public void parseTrips() {
//		System.out.println("[INFO] Parsing trips...");
		try {
			final BufferedReader br = new BufferedReader(new FileReader(new File(gtfs + "trips.txt")));
			br.readLine();
			String s = br.readLine();
			StringTokenizer st;
			String script = "";
			script = "INSERT INTO vdv_gtfs_tmp.trips VALUES\n";
			while (s != null) {
				st = new StringTokenizer(s, ",");
				script += "('" + Integer.parseInt(st.nextToken()) + "', '" + Integer.parseInt(st.nextToken()) + "', '" + Integer.parseInt(st.nextToken()) + "')";
				s = br.readLine();
				if (s != null) {
					script += ",\n";
				}
			}
			script += ";";
//			System.out.println(script);
			final Statement stmt = DBConnector.getConnection().createStatement();
			stmt.execute(script);
			br.close();
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
//		System.out.println("[INFO] Parsing trips...Done.");
	}

	public void parseTripSequence() {
//		System.out.println("[INFO] Parsing trips sequence...");
		try {
			final BufferedReader br = new BufferedReader(new FileReader(new File(gtfs + "stop_times.txt")));
			StringTokenizer st = null;
			br.readLine();
			String s = br.readLine();
			String script = "";
//			script =  "INSERT INTO vdv_gtfs_tmp.stop_times VALUES\n";
			final Statement stmt = DBConnector.getConnection().createStatement();
			while (s != null) {
				script = "INSERT INTO vdv_gtfs_tmp.stop_times VALUES\n";
				st = new StringTokenizer(s, ",");
				script += "('" + Integer.parseInt(st.nextToken()) + "', '" + Integer.parseInt(st.nextToken()) + "', '" + st.nextToken() + "', '" + st.nextToken() + "', '"
					+ Integer.parseInt(st.nextToken()) + "')";
				s = br.readLine();
				//				if(s != null)
				//					script += ",\n";
				script += ";";
				stmt.execute(script);
			}

//			System.out.println(script);
			br.close();
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
//		System.out.println("[INFO] Parsing trips sequence...Done.");
	}

	public void parseCalendar() {
//		System.out.println("[INFO] Parsing calendar...");
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(gtfs + "calendar.txt")));
			StringTokenizer st = null;
			br.readLine();
			String s = br.readLine();
			String script = "";
			script = "INSERT INTO vdv_gtfs_tmp.calendar(service_id," + "monday," + "tuesday," + "wednesday," + "thursday," + "friday," + "saturday," + "sunday," + "start_date," + "end_date) VALUES\n";
			while (s != null) {
				st = new StringTokenizer(s, ",");
				script += "('" + Integer.parseInt(st.nextToken()) + "', '" + isValidDay(st.nextToken()) + "', '" + isValidDay(st.nextToken()) + "', '" + isValidDay(st.nextToken()) + "', '"
					+ isValidDay(st.nextToken()) + "', '" + isValidDay(st.nextToken()) + "', '" + isValidDay(st.nextToken()) + "', '" + isValidDay(st.nextToken()) + "', '" + st.nextToken() + "', '"
					+ st.nextToken() + "'),\n";
				s = br.readLine();
			}
			br.close();

			br = new BufferedReader(new FileReader(new File(gtfs + "calendar_dates.txt")));
			br.readLine();
			s = br.readLine();
			Calendar c = null;
			while (s != null) {
				st = new StringTokenizer(s, ",");
				final int id = Integer.parseInt(st.nextToken());
//				System.out.println(id);
				final String date = st.nextToken();
//				System.out.println(date);
				c = parseDateToCalendar(date);
				final boolean[] validity = { false, false, false, false, false, false, false };
				validity[c.get(Calendar.DAY_OF_WEEK) - 1] = true;
				script += "('" + id + "', '" + validity[0] + "', '" + validity[1] + "', '" + validity[2] + "', '" + validity[3] + "', '" + validity[4] + "', '" + validity[5] + "', '" + validity[6]
					+ "', '" + date + "', '" + date + "')";
				s = br.readLine();
				if (s != null) {
					script += ",\n";
				}
			}
			script += ";";
//			System.out.println(script);
			final Statement stmt = DBConnector.getConnection().createStatement();
			stmt.execute(script);
			br.close();
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
//		System.out.println("[INFO] Parsing calendar...Done.");
	}

	public boolean isValidDay(final String valid) {
		return  !valid.equals("0");
	}

	public String parseDate(final String d) {
		return d.substring(0, 4) + "-" + d.substring(4, 6) + "-" + d.substring(6, 8);
	}

	public long parseDateToMillis(final String d) {
		//		System.out.println(d);
		final Calendar c = new GregorianCalendar(Integer.parseInt(d.substring(0, 4)), Integer.parseInt(d.substring(4, 6)), Integer.parseInt(d.substring(6, 8)));
		return c.getTimeInMillis();
	}

	public Calendar parseDateToCalendar(final String d) {
		final Calendar c = new GregorianCalendar(Integer.parseInt(d.substring(0, 4)), Integer.parseInt(d.substring(4, 6)), Integer.parseInt(d.substring(6, 8)));
		return c;
	}

	public void createCalendar() {
		final List<Service> services = db.getCalendars();
		Calendar start = null;
		Calendar end = null;
		int doyStart, doyEnd, dow = -1;
		int[] validityVector = null;
		boolean[] validity = null;
		String dbVector = null;
		for (final Service s : services) {
			start = new GregorianCalendar();
			end = new GregorianCalendar();
			start.setFirstDayOfWeek(Calendar.MONDAY);
			end.setFirstDayOfWeek(Calendar.MONDAY);
			validityVector = new int[366];
			dbVector = new String();
			for (int i = 0; i < 366; i++) {
				validityVector[i] = 0;
			}
			start.setTimeInMillis(parseDateToMillis(s.getStartDate()));
			end.setTimeInMillis(parseDateToMillis(s.getEndDate()));
			doyStart = start.get(Calendar.DAY_OF_YEAR);
			doyEnd = end.get(Calendar.DAY_OF_YEAR);
			validity = s.getValidity();
			final Calendar c = (Calendar) start.clone();
			while (doyStart <= doyEnd) {
				dow = c.get(Calendar.DAY_OF_WEEK);
				if (validity[dow - 1]) {
					validityVector[doyStart - 1] = 1;
				}
				c.add(Calendar.DAY_OF_YEAR, +1);
				doyStart = c.get(Calendar.DAY_OF_YEAR);
			}
			for (int i = 0; i < 366; i++) {
				dbVector += validityVector[i];
			}
			db.insertService(s.getId(), s.getStartDate(), s.getEndDate(), dbVector, city);
		}
	}

}
