package datamodel.tmpdb;

import datamodel.util.DbConnector;
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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
public class BusDataParser {
	private static final Charset FILE_CS = Charset.forName("UTF-8");
	private static final int LENGTH_OF_YEAR = 366;
	private DbConnector db;
	private String folder;
	private String city;

	// Constructor

	public BusDataParser(final DbConnector db, final String folder, final String city) {
		this.db = db;
		this.folder = folder + File.separatorChar;
		this.city = city;
	}

	// Public methods

	// TODO: What about deletion of tables before parsing them?
	public void parseStops() {
//		System.out.println("[INFO] Parsing bus stops...");

		final StringBuilder script = new StringBuilder();
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
				script.append("INSERT INTO vdv_gtfs_tmp.stops VALUES (" + id + ", '" + name + "', " + lat + ", " + longi + ");");
			}
		} catch (final IOException e) {
			e.printStackTrace();
			return;
		}

		try (Statement stmt = db.getConnection().createStatement()) {
			stmt.execute(script.toString());
		} catch (final SQLException e1) {
			e1.printStackTrace();
		}

//		System.out.println("[INFO] Done.");
	}

	public void parseRoutes() {
//		System.out.println("[INFO] Parsing routes...");

		final StringBuilder script = new StringBuilder("INSERT INTO vdv_gtfs_tmp.routes VALUES\n");
		try (final BufferedReader br = getBufferedReader(new File(folder + "routes.txt"))) {
			br.readLine();
			String s = br.readLine();
			while (s != null) {
				final StringTokenizer st = new StringTokenizer(s, ",");
				st.nextToken();
				script.append("('");
				script.append(Integer.parseInt(st.nextToken()) + "', '");
				script.append(st.nextToken() + "', '");
				script.append(st.nextToken());
				script.append("')");
//				db.insertRoute(Integer.parseInt(st.nextToken()), st.nextToken(), st.nextToken());

				s = br.readLine();
				if (s != null) {
					script.append(",\n");
				}
			}
			script.append(";");
		} catch (final IOException e) {
			e.printStackTrace();
			return;
		}

		try (final Statement stmt = db.getConnection().createStatement()) {
			stmt.execute(script.toString());
		} catch (final SQLException e) {
			e.printStackTrace();
		}

//		System.out.println("[INFO] Done.");
	}

	public void parseTrips() {
//		System.out.println("[INFO] Parsing trips...");

		final StringBuilder script = new StringBuilder("INSERT INTO vdv_gtfs_tmp.trips VALUES\n");
		try (final BufferedReader br = getBufferedReader(new File(folder + "trips.txt"))) {
			br.readLine();
			String s = br.readLine();
			while (s != null) {
				final StringTokenizer st = new StringTokenizer(s, ",");
				script.append("('" + Integer.parseInt(st.nextToken()) + "', '" + Integer.parseInt(st.nextToken()) + "', '" + Integer.parseInt(st.nextToken()) + "')");

				s = br.readLine();
				if (s != null) {
					script.append(",\n");
				}
			}
			script.append(";");
		} catch (final IOException e) {
			e.printStackTrace();
			return;
		}

		try (final Statement stmt = db.getConnection().createStatement()) {
			stmt.execute(script.toString());
		} catch (final SQLException e) {
			e.printStackTrace();
		}

//		System.out.println("[INFO] Done.");
	}

	public void parseTripSequence() {
//		System.out.println("[INFO] Parsing trips sequence...");

		final StringBuilder script = new StringBuilder("INSERT INTO vdv_gtfs_tmp.stop_times VALUES\n");
		try (final BufferedReader br = getBufferedReader(new File(folder + "stop_times.txt"))) {
			br.readLine();
			String s = null;
			while ((s = br.readLine()) != null) {
				final StringTokenizer st = new StringTokenizer(s, ",");
				script.append("('");
				script.append(Integer.parseInt(st.nextToken()) + "', '");
				script.append(Integer.parseInt(st.nextToken()) + "', '");
				script.append(st.nextToken() + "', '");
				script.append(st.nextToken() + "', '");
				script.append(Integer.parseInt(st.nextToken()));
				script.append("');");
			}
		} catch (final IOException e) {
			e.printStackTrace();
			return;
		}

		try (final Statement stmt = db.getConnection().createStatement()) {
			stmt.execute(script.toString());
		} catch (final SQLException e) {
			e.printStackTrace();
		}

//		System.out.println("[INFO] Done.");
	}

	public void parseCalendar() {
//		System.out.println("[INFO] Parsing calendar...");

		final StringBuilder script = new StringBuilder("INSERT INTO vdv_gtfs_tmp.calendar(service_id, monday, tuesday, wednesday, thursday, friday, saturday, sunday, start_date, end_date) VALUES\n");
		try (final BufferedReader br = getBufferedReader(new File(folder + "calendar.txt"))) {
			br.readLine();
			String s = null;
			while ((s = br.readLine()) != null) {
				final StringTokenizer st = new StringTokenizer(s, ",");
				script.append("('");
				script.append(Integer.parseInt(st.nextToken()) + "', '");
				script.append(isValidDay(st.nextToken()) + "', '");
				script.append(isValidDay(st.nextToken()) + "', '");
				script.append(isValidDay(st.nextToken()) + "', '");
				script.append(isValidDay(st.nextToken()) + "', '");
				script.append(isValidDay(st.nextToken()) + "', '");
				script.append(isValidDay(st.nextToken()) + "', '");
				script.append(isValidDay(st.nextToken()) + "', '");
				script.append(st.nextToken() + "', '");
				script.append(st.nextToken());
				script.append("'),\n");
			}
		} catch (final IOException e) {
			e.printStackTrace();
			return;
		}

		try (final BufferedReader br = getBufferedReader(new File(folder + "calendar_dates.txt"))) {
			br.readLine();
			String s = br.readLine();
			while (s != null) {
				final StringTokenizer st = new StringTokenizer(s, ",");
				final int id = Integer.parseInt(st.nextToken());
				final String date = st.nextToken();
				final Calendar c = parseDateToCalendar(date);

				final boolean[] v = { false, false, false, false, false, false, false };
				v[c.get(Calendar.DAY_OF_WEEK) - 1] = true;

				// CHECKSTYLE:OFF MagicNumber
				script.append("('");
				script.append(id + "', '");
				script.append(v[0] + "', '");
				script.append(v[1] + "', '");
				script.append(v[2] + "', '");
				script.append(v[3] + "', '");
				script.append(v[4] + "', '");
				script.append(v[5] + "', '");
				script.append(v[6] + "', '");
				script.append(date + "', '");
				script.append(date);
				script.append("')");
				// CHECKSTYLE:ON MagicNumber

				s = br.readLine();
				if (s != null) {
					script.append(",\n");
				}
			}
			script.append(";");
		} catch (final IOException e) {
			e.printStackTrace();
			return;
		}

		try (final Statement stmt = db.getConnection().createStatement()) {
			stmt.execute(script.toString());
		} catch (final SQLException e) {
			e.printStackTrace();
		}

//		System.out.println("Done.");
	}

	public void createCalendar() {
		final Collection<Service> services = getCalendars();
		final Calendar end = new GregorianCalendar();
		end.setFirstDayOfWeek(Calendar.MONDAY);

		final Calendar start = new GregorianCalendar();
		start.setFirstDayOfWeek(Calendar.MONDAY);

		for (final Service s : services) {
			end.setTimeInMillis(parseDateToMillis(s.getEndDate()));
			final int doyEnd = end.get(Calendar.DAY_OF_YEAR);

			start.setTimeInMillis(parseDateToMillis(s.getStartDate()));
			int doyStart = start.get(Calendar.DAY_OF_YEAR);

			final boolean[] validity = s.getValidity();
			final Calendar c = (Calendar) start.clone();
			final int[] validityVector = new int[LENGTH_OF_YEAR];
			while (doyStart <= doyEnd) {
				if (validity[c.get(Calendar.DAY_OF_WEEK) - 1]) {
					validityVector[doyStart - 1] = 1;
				}
				c.add(Calendar.DAY_OF_YEAR, +1);
				doyStart = c.get(Calendar.DAY_OF_YEAR);
			}

			final StringBuilder dbVector = new StringBuilder();
			for (int i = 0; i < validityVector.length; i++) {
				dbVector.append(validityVector[i]);
			}

			db.insertService(s.getId(), s.getStartDate(), s.getEndDate(), dbVector.toString(), city);
		}
	}

	// Private methods

	private Collection<Service> getCalendars() {
		final String query = "SELECT * FROM vdv_gtfs_tmp.calendar";
		final Collection<Service> services = new ArrayList<Service>();
		try (
			final PreparedStatement stmt = db.getConnection().prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			final ResultSet rs = stmt.executeQuery()
		) {
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
		} catch (final SQLException e) {
			e.printStackTrace();
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

	private static long parseDateToMillis(final String d) {
		final Calendar c = new GregorianCalendar(Integer.parseInt(d.substring(0, 4)), Integer.parseInt(d.substring(4, 6)), Integer.parseInt(d.substring(6, 8)));
		return c.getTimeInMillis();
	}

}
