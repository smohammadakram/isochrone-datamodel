package datamodel.impl.bus;

import datamodel.db.DbConnector;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.onebusaway.gtfs.impl.GtfsRelationalDaoImpl;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.ServiceCalendar;
import org.onebusaway.gtfs.model.ServiceCalendarDate;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;
import org.onebusaway.gtfs.model.calendar.ServiceDate;
import org.onebusaway.gtfs.serialization.GtfsReader;

@SuppressFBWarnings(
	value = {"SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE", "SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING"},
	justification = "Since we need to fill in the name of the city as table name prefix this can not be done in another way"
)
public class BusNetwork {

	private static final Logger LOGGER = LogManager.getLogger(BusNetwork.class);
	private static final String TABLE_NAME = "time_expanded.tmp_";
	private final DbConnector db;
	private final String city;
	private boolean calendarTruncated = false;
	private GtfsRelationalDaoImpl entityStore;

	// Constructor

	public BusNetwork(final DbConnector db, final String folder, final String city) throws IOException {
		this(db, city);
		initGtfs(folder);
	}

	public BusNetwork(final DbConnector db, final String city) {
		this.db = db;
		this.city = city;
	}

	// Public methods

	public void initGtfs(final String path) throws IOException {
		entityStore = new GtfsRelationalDaoImpl();
		entityStore.setGenerateIds(true);

		final GtfsReader reader = new GtfsReader();
		reader.setInputLocation(new File(path));
		reader.setEntityStore(entityStore);
		reader.run();
	}

	public void insertBusCalendar(final boolean truncateFirst) throws SQLException {
		LOGGER.info("Copying bus calendar to time_expanded." + city + "_bus_calendar...");

		if (truncateFirst) {
			final String queryTruncate = "TRUNCATE TABLE time_expanded.%s_bus_calendar";
			db.execute(String.format(Locale.ENGLISH, queryTruncate, city));
		}
		final String query = "INSERT INTO time_expanded.%s_bus_calendar(service_id, service_start_date, service_end_date, service_vector) VALUES(?,?,?,?)";
		final String cQuery = String.format(Locale.ENGLISH, query, city);

		final Collection<Service> services = getCalendarEntries();
		try (final PreparedStatement stmt = db.getPreparedStatement(cQuery)) {
			for (final Service service : services) {
				// CHECKSTYLE:OFF MagicNumber
				stmt.setString(1, service.getId());
				stmt.setString(2, service.getStartDate());
				stmt.setString(3, service.getEndDate());
				stmt.setString(4, service.getValidityVector());
				// CHECKSTYLE:ON MagicNumber

				stmt.addBatch();
			}

			stmt.executeBatch();
		}

		LOGGER.info("Done.");
	}

	public String parseCalendar() {
		LOGGER.info("Parsing calendar...");

		final String sqlCommand = "INSERT INTO " + TABLE_NAME + "calendar(service_id, monday, tuesday, wednesday, thursday, friday, saturday, sunday, start_date, end_date) VALUES (";
		final StringBuilder script = new StringBuilder();
		if (!calendarTruncated) {
			calendarTruncated = true;
			script.append("TRUNCATE TABLE " + TABLE_NAME + "calendar;\n\n");
		}

		final Collection<ServiceCalendar> calendars = entityStore.getAllCalendars();
		for (final ServiceCalendar c : calendars) {
			script.append(sqlCommand + "'");
			script.append(escape(c.getServiceId()) + "', ");
			script.append(isValidDay(c.getMonday()) + ", ");
			script.append(isValidDay(c.getTuesday()) + ", ");
			script.append(isValidDay(c.getWednesday()) + ", ");
			script.append(isValidDay(c.getThursday()) + ", ");
			script.append(isValidDay(c.getFriday()) + ", ");
			script.append(isValidDay(c.getSaturday()) + ", ");
			script.append(isValidDay(c.getSunday()) + ", '");
			script.append(c.getStartDate().getAsString() + "', '");
			script.append(c.getEndDate().getAsString());
			script.append("');\n");
		}

		LOGGER.info("Done.");
		return script.toString();
	}

	public String parseCalendarDates() {
		LOGGER.info("Parsing calendar dates...");

		final String sqlCommand = "INSERT INTO " + TABLE_NAME + "calendar(service_id, monday, tuesday, wednesday, thursday, friday, saturday, sunday, start_date, end_date) VALUES (";
		final StringBuilder script = new StringBuilder();
		if (!calendarTruncated) {
			calendarTruncated = true;
			script.append("TRUNCATE TABLE " + TABLE_NAME + "calendar;\n\n");
		}

		final Collection<ServiceCalendarDate> calendarDates = entityStore.getAllCalendarDates();
		for (final ServiceCalendarDate date : calendarDates) {
			final ServiceDate d = date.getDate();
			final String cStr = d.getAsString();
			final boolean[] v = { false, false, false, false, false, false, false };
			v[d.getAsCalendar(TimeZone.getTimeZone("GMT")).get(Calendar.DAY_OF_WEEK) - 1] = true;

			// CHECKSTYLE:OFF MagicNumber
			script.append(sqlCommand + "'");
			script.append(escape(date.getServiceId()) + "', '");
			script.append(v[0] + "', '");
			script.append(v[1] + "', '");
			script.append(v[2] + "', '");
			script.append(v[3] + "', '");
			script.append(v[4] + "', '");
			script.append(v[5] + "', '");
			script.append(v[6] + "', '");
			script.append(cStr + "', '");
			script.append(cStr);
			script.append("');\n");
			// CHECKSTYLE:ON MagicNumber
		}

		LOGGER.info("Done.");
		return script.toString();
	}

	public String parseRoutes() {
		LOGGER.info("Parsing routes...");

		final String sqlCommand = "INSERT INTO " + TABLE_NAME + "routes VALUES (";
		final StringBuilder script = new StringBuilder("TRUNCATE TABLE " + TABLE_NAME + "routes;\n\n");

		final Collection<Route> routes = entityStore.getAllRoutes();
		for (final Route r : routes) {
			script.append(sqlCommand + "'");
			script.append(escape(r.getId()) + "', '");
			script.append(escape(r.getLongName()) + "', '");
			script.append(escape(r.getShortName()));
			script.append("');\n");
		}

		LOGGER.info("Done.");
		return script.toString();
	}

	public String parseStops() {
		LOGGER.info("Parsing bus stops...");

		final String sqlCommand = "INSERT INTO " + TABLE_NAME + "stops VALUES (";
		final StringBuilder script = new StringBuilder("TRUNCATE " + TABLE_NAME + "stops;\n\n");

		final Collection<Stop> stops = entityStore.getAllStops();
		for (final Stop s : stops) {
			script.append(sqlCommand + "'");
			script.append(escape(s.getId()) + "', '");
			script.append(escape(s.getName()) + "', ");
			script.append(s.getLat() + ", ");
			script.append(s.getLon());
			script.append(");\n");
		}

		LOGGER.info("Done.");
		return script.toString();
	}

	public String parseTrips() {
		LOGGER.info("Parsing trips...");

		final String sqlCommand = "INSERT INTO " + TABLE_NAME + "trips VALUES (";
		final StringBuilder script = new StringBuilder("TRUNCATE " + TABLE_NAME + "trips;\n\n");

		final Collection<Trip> trips = entityStore.getAllTrips();
		for (final Trip t : trips) {
			script.append(sqlCommand + "'");
			script.append(escape(t.getRoute().getId()) + "', '");
			script.append(escape(t.getId()) + "', '");
			script.append(escape(t.getServiceId()));
			script.append("');\n");
		}

		LOGGER.info("Done.");
		return script.toString();
	}

	public String parseStopTimes() {
		LOGGER.info("Parsing stop times...");

		final String sqlCommand = "INSERT INTO " + TABLE_NAME + "stop_times VALUES (";
		final StringBuilder script = new StringBuilder("TRUNCATE " + TABLE_NAME + "stop_times;\n\n");

		final Collection<StopTime> times = entityStore.getAllStopTimes();
		for (final StopTime t : times) {
			script.append(sqlCommand + "'");
			script.append(escape(t.getTrip().getId()) + "', '");
			script.append(escape(t.getStop().getId()) + "', ");
			script.append(t.getArrivalTime() + ", ");
			script.append(t.getDepartureTime() + ", ");
			script.append(t.getStopSequence());
			script.append(");\n");
		}

		LOGGER.info("Done.");
		return script.toString();
	}

	// Private methods

	private Collection<Service> getCalendarEntries() throws SQLException {
		final String query = "SELECT * FROM " + TABLE_NAME + "calendar";
		final Collection<Service> services = new ArrayList<Service>();
		try (
			final Statement stmt = db.getStatement();
			final ResultSet rs = stmt.executeQuery(query)
		) {
			while (rs.next()) {
				final Service s = new Service(rs.getString(1));
				// CHECKSTYLE:OFF MagicNumber
				s.setStartDate(rs.getString(2));
				s.setEndDate(rs.getString(3));
				s.setValidity(rs.getBoolean(4), rs.getBoolean(5), rs.getBoolean(6), rs.getBoolean(7), rs.getBoolean(8), rs.getBoolean(9), rs.getBoolean(10));
				// CHECKSTYLE:ON MagicNumber

				services.add(s);
			}
		}

		return services;
	}

	// Private static methods

	private static String escape(final AgencyAndId id) {
		if (id == null) {
			return null;
		}

		return escape(id.toString());
	}

	private static String escape(final String str) {
		if (str == null) {
			return null;
		}

		return str.replaceAll("'", "''");
	}

	private static boolean isValidDay(final int valid) {
		return valid != 0;
	}

}
