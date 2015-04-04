package datamodel.impl.bus;

import datamodel.TestHelper;
import datamodel.db.DbConnector;
import datamodel.db.DbScript;
import datamodel.impl.ScriptGenerator;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class BusNetworkTest {
	private boolean performInserts = false;
	private DbConnector db;
	private Map<String, BusNetwork> testMap;

	@BeforeClass
	public void setup() throws IOException {
		// Creates database tables (or truncates existing ones by dropping and re-creating)
		Assert.assertTrue(ScriptGenerator.executeScript("datamodel/impl/bus/bus_network.sql"), "Setup failed");
		Assert.assertTrue(ScriptGenerator.executeScript("datamodel/db/tmp-create.sql"), "Setup failed");

		db = new DbConnector();
		testMap = new HashMap<>(2);
		testMap.put(TestHelper.TEST_CITY, new BusNetwork(db, TestHelper.TEST_GTFS, TestHelper.TEST_CITY));
		testMap.put("SanFrancisco", new BusNetwork(db, "src/test/resources/gtfs_sanfrancisco.zip", "SanFrancisco"));
	}

	@AfterClass
	public void tearDown() {
		if (db != null) {
			db.close();
		}
	}

	// Test methods

	@Test
	public void testCalendar() {
		testMap.entrySet().forEach(e -> testEntry(e.getKey(), e.getValue()::parseCalendar, "calendar.sql"));
	}

	@Test
	public void testCalendarDates() {
		testMap.entrySet().forEach(e -> testEntry(e.getKey(), e.getValue()::parseCalendarDates, "calendar_dates.sql"));
	}

	@Test
	public void testRoutes() {
		testMap.entrySet().forEach(e -> testEntry(e.getKey(), e.getValue()::parseRoutes, "routes.sql"));
	}

	@Test
	public void testStops() {
		testMap.entrySet().forEach(e -> testEntry(e.getKey(), e.getValue()::parseStops, "stops.sql"));
	}

	@Test
	public void testStopTimes() {
		testMap.entrySet().forEach(e -> testEntry(e.getKey(), e.getValue()::parseStopTimes, "stop_times.sql"));
	}

	@Test
	public void testTrips() {
		testMap.entrySet().forEach(e -> testEntry(e.getKey(), e.getValue()::parseTrips, "trips.sql"));
	}

	// Private methods

	private void testEntry(final String cityName, final Supplier<String> s, final String filename) {
		final String sql = s.get();

		Assert.assertNotNull(sql);
		Assert.assertTrue(sql.length() > 0);
		try {
			postProcess(cityName + "_" + filename, sql);
		} catch (final Exception e) {
			Assert.fail("An exception occurred during post processing", e);
		}
	}

	private void postProcess(final String filename, final String sql) throws SQLException, IOException {
		final String resultFolder = TestHelper.TEST_OUTPUT_SCRIPT + File.separatorChar + TestHelper.TEST_CITY + "_gtfs";
		ScriptGenerator.write2File(resultFolder + File.separatorChar + filename, sql);

		if (db != null && performInserts) {
			db.executeScript(new DbScript(sql, true));
		}
	}

}
