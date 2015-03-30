package datamodel.impl.bus;

import datamodel.db.DbConnector;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

@SuppressFBWarnings(
	value = {"SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE", "SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING"},
	justification = "Since we need to fill in the name of the city as table name prefix this can not be done in another way"
)
public class BusNetwork {
	private static final Charset FILE_CS = Charset.forName("UTF-8");
	private static final String TABLE_NAME = "vdv_gtfs_tmp";
	private boolean calendarTruncated = false;
	private DbConnector db;
	private String folder;
	private String city;

	// Constructor

	public BusNetwork(final DbConnector db, final String folder, final String city) {
		this.db = db;
		this.folder = folder + File.separatorChar;
		this.city = city;
	}

	// Public methods

	public void copyBusCalendarTable() throws SQLException {
		copyBusCalendarTable(true);
	}

	public void copyBusCalendarTable(final boolean truncateFirst) throws SQLException {
		if (truncateFirst) {
			final String queryTruncate = "TRUNCATE TABLE time_expanded.%s_bus_calendar";
			db.execute(String.format(queryTruncate, city));
		}
		final String query = "INSERT INTO time_expanded.%s_bus_calendar(service_id, service_start_date, service_end_date, service_vector) VALUES(?,?,?,?)";
		final String cQuery = String.format(query, city);

		final Collection<Service> services = getCalendarEntries();
		try (final PreparedStatement stmt = db.getPreparedStatement(cQuery)) {
			for (final Service service : services) {
				// CHECKSTYLE:OFF MagicNumber
				stmt.setInt(1, service.getId());
				stmt.setString(2, service.getStartDate());
				stmt.setString(3, service.getEndDate());
				stmt.setString(4, service.getValidityVector());
				// CHECKSTYLE:ON MagicNumber

				stmt.addBatch();
			}

			stmt.executeBatch();
		}
	}

	public String parseCalendar() throws SQLException {
//		System.out.println("[INFO] Parsing calendar...");

		final String sqlCommand = "INSERT INTO " + TABLE_NAME + ".calendar(service_id, monday, tuesday, wednesday, thursday, friday, saturday, sunday, start_date, end_date) VALUES (";
		final StringBuilder script = new StringBuilder();
		if (!calendarTruncated) {
			calendarTruncated = true;
			script.append("TRUNCATE TABLE " + TABLE_NAME + ".calendar;\n\n");
		}

		try (final BufferedReader br = getBufferedReader(new File(folder + "calendar.txt"))) {
			br.readLine();
			String s = null;
			while ((s = br.readLine()) != null) {
				final StringTokenizer st = new StringTokenizer(s, ",");
				script.append(sqlCommand);
				script.append(Integer.parseInt(st.nextToken()) + ", '");
				script.append(isValidDay(st.nextToken()) + "', '");
				script.append(isValidDay(st.nextToken()) + "', '");
				script.append(isValidDay(st.nextToken()) + "', '");
				script.append(isValidDay(st.nextToken()) + "', '");
				script.append(isValidDay(st.nextToken()) + "', '");
				script.append(isValidDay(st.nextToken()) + "', '");
				script.append(isValidDay(st.nextToken()) + "', '");
				script.append(st.nextToken() + "', '");
				script.append(st.nextToken());
				script.append("');\n");
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}

//		System.out.println("Done.");
		return script.toString();
	}

	public String parseCalendarDates() throws SQLException {
//		System.out.println("[INFO] Parsing calendar dates...");

		final String sqlCommand = "INSERT INTO " + TABLE_NAME + ".calendar(service_id, monday, tuesday, wednesday, thursday, friday, saturday, sunday, start_date, end_date) VALUES (";
		final StringBuilder script = new StringBuilder();
		if (!calendarTruncated) {
			calendarTruncated = true;
			script.append("TRUNCATE TABLE " + TABLE_NAME + ".calendar;\n\n");
		}

		try (final BufferedReader br = getBufferedReader(new File(folder + "calendar_dates.txt"))) {
			br.readLine();
			String s = null;
			while ((s = br.readLine()) != null) {
				final StringTokenizer st = new StringTokenizer(s, ",");
				final int id = Integer.parseInt(st.nextToken());
				final String date = st.nextToken();
				final Calendar c = parseDateToCalendar(date);

				final boolean[] v = { false, false, false, false, false, false, false };
				v[c.get(Calendar.DAY_OF_WEEK) - 1] = true;

				// CHECKSTYLE:OFF MagicNumber
				script.append(sqlCommand);
				script.append(id + ", '");
				script.append(v[0] + "', '");
				script.append(v[1] + "', '");
				script.append(v[2] + "', '");
				script.append(v[3] + "', '");
				script.append(v[4] + "', '");
				script.append(v[5] + "', '");
				script.append(v[6] + "', '");
				script.append(date + "', '");
				script.append(date);
				script.append("');\n");
				// CHECKSTYLE:ON MagicNumber
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}

//		System.out.println("Done.");
		return script.toString();
	}

	public String parseRoutes() throws SQLException {
//		System.out.println("[INFO] Parsing routes...");

		final String sqlCommand = "INSERT INTO " + TABLE_NAME + ".routes VALUES (";
		final StringBuilder script = new StringBuilder("TRUNCATE TABLE " + TABLE_NAME + ".routes;\n\n");

		try (final BufferedReader br = getBufferedReader(new File(folder + "routes.txt"))) {
			br.readLine();
			String s = null;
			while ((s =  br.readLine()) != null) {
				final StringTokenizer st = new StringTokenizer(s, ",");
				st.nextToken();
				script.append(sqlCommand);
				script.append(Integer.parseInt(st.nextToken()) + ", '");
				script.append(st.nextToken() + "', '");
				script.append(st.nextToken());
				script.append("');\n");
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}

//		System.out.println("[INFO] Done.");
		return script.toString();
	}

	public String parseStops() throws SQLException {
//		System.out.println("[INFO] Parsing bus stops...");

		final String sqlCommand = "INSERT INTO " + TABLE_NAME + ".stops VALUES (";
		final StringBuilder script = new StringBuilder("TRUNCATE " + TABLE_NAME + ".stops;\n\n");

		try (final BufferedReader br = getBufferedReader(new File(folder + "stops.txt"))) {
			br.readLine(); // first line skipped
			String line = null;
			while ((line = br.readLine()) != null) {
				final StringTokenizer st = new StringTokenizer(line, ",");
				final int id = Integer.parseInt(st.nextToken());
				final String name = st.nextToken().replace("\'", " ");
				final String s = st.nextToken() + "." + st.nextToken();
				final String s1 = st.nextToken() + "." + st.nextToken();
				final double lat = Double.parseDouble(s.substring(1, s.length() - 2));
				final double longi = Double.parseDouble(s1.substring(1, s.length() - 2));

				script.append(sqlCommand);
				script.append(id + ", '");
				script.append(name + "', ");
				script.append(lat + ", ");
				script.append(longi);
				script.append(");\n");
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}

//		System.out.println("[INFO] Done.");
		return script.toString();
	}

	public String parseTrips() throws SQLException {
//		System.out.println("[INFO] Parsing trips...");

		final String sqlCommand = "INSERT INTO " + TABLE_NAME + ".trips VALUES (";
		final StringBuilder script = new StringBuilder("TRUNCATE " + TABLE_NAME + ".trips;\n\n");

		try (final BufferedReader br = getBufferedReader(new File(folder + "trips.txt"))) {
			br.readLine();
			String s = null;
			while ((s = br.readLine()) != null) {
				final StringTokenizer st = new StringTokenizer(s, ",");
				script.append(sqlCommand);
				script.append(Integer.parseInt(st.nextToken()) + ", ");
				script.append(Integer.parseInt(st.nextToken()) + ", ");
				script.append(Integer.parseInt(st.nextToken()));
				script.append(");\n");
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}

//		System.out.println("[INFO] Done.");
		return script.toString();
	}

	public String parseStopTimes() throws SQLException {
//		System.out.println("[INFO] Parsing stop times...");

		final String sqlCommand = "INSERT INTO " + TABLE_NAME + ".stop_times VALUES (";
		final StringBuilder script = new StringBuilder("TRUNCATE " + TABLE_NAME + ".stop_times;\n\n");

		try (final BufferedReader br = getBufferedReader(new File(folder + "stop_times.txt"))) {
			br.readLine();
			String s = null;
			while ((s = br.readLine()) != null) {
				final StringTokenizer st = new StringTokenizer(s, ",");
				script.append(sqlCommand);
				script.append(Integer.parseInt(st.nextToken()) + ", ");
				script.append(Integer.parseInt(st.nextToken()) + ", '");
				script.append(st.nextToken() + "', '");
				script.append(st.nextToken() + "', ");
				script.append(Integer.parseInt(st.nextToken()));
				script.append(");\n");
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}

//		System.out.println("[INFO] Done.");
		return script.toString();
	}

	// Private methods

	private Collection<Service> getCalendarEntries() throws SQLException {
		final String query = "SELECT * FROM " + TABLE_NAME + ".calendar";
		final Collection<Service> services = new ArrayList<Service>();
		try (final ResultSet rs = db.executeQuery(query)) {
			if (rs.first()) {
				while (rs.next()) {
					final Service s = new Service(rs.getInt(1));
					// CHECKSTYLE:OFF MagicNumber
					s.setStartDate(rs.getString(2));
					s.setEndDate(rs.getString(3));
					s.setValidity(rs.getBoolean(4), rs.getBoolean(5), rs.getBoolean(6), rs.getBoolean(7), rs.getBoolean(8), rs.getBoolean(9), rs.getBoolean(10));
					// CHECKSTYLE:ON MagicNumber

					services.add(s);
				}
			}
		}

		return services;
	}

	// Private static methods

	private static BufferedReader getBufferedReader(final File f) throws FileNotFoundException {
		return new BufferedReader(new InputStreamReader(new FileInputStream(f), FILE_CS));
	}

	private static boolean isValidDay(final String valid) {
		return !valid.equals("0");
	}

	private static Calendar parseDateToCalendar(final String d) {
		// CHECKSTYLE:OFF MagicNumber
		return new GregorianCalendar(Integer.parseInt(d.substring(0, 4)), Integer.parseInt(d.substring(4, 6)), Integer.parseInt(d.substring(6, 8)));
		// CHECKSTYLE:ON MagicNumber
	}

}
