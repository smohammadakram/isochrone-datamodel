package datamodel.impl.bus;

import datamodel.TestHelper;
import datamodel.db.DbConnector;
import datamodel.impl.ScriptGenerator;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
	public void testCalendar() throws SQLException, IOException {
		final Set<Entry<String, BusNetwork>> entrySet = testMap.entrySet();
		for (final Entry<String, BusNetwork> entry : entrySet) {
			final String cityName = entry.getKey();
			final BusNetwork bn = entry.getValue();
			final String sql = bn.parseCalendar();

			Assert.assertNotNull(sql);
			Assert.assertTrue(sql.length() > 0);
			postProcess(cityName + "_calendar.sql", sql);
		}
	}

	@Test
	public void testCalendarDates() throws SQLException, IOException {
		final Set<Entry<String, BusNetwork>> entrySet = testMap.entrySet();
		for (final Entry<String, BusNetwork> entry : entrySet) {
			final String cityName = entry.getKey();
			final BusNetwork bn = entry.getValue();
			final String sql = bn.parseCalendarDates();

			Assert.assertNotNull(sql);
			Assert.assertTrue(sql.length() > 0);
			postProcess(cityName + "_calendar_dates.sql", sql);
		}
	}

	@Test
	public void testRoutes() throws SQLException, IOException {
		final Set<Entry<String, BusNetwork>> entrySet = testMap.entrySet();
		for (final Entry<String, BusNetwork> entry : entrySet) {
			final String cityName = entry.getKey();
			final BusNetwork bn = entry.getValue();
			final String sql = bn.parseRoutes();

			Assert.assertNotNull(sql);
			Assert.assertTrue(sql.length() > 0);
			postProcess(cityName + "_routes.sql", sql);
		}
	}

	@Test
	public void testStops() throws SQLException, IOException {
		final Set<Entry<String, BusNetwork>> entrySet = testMap.entrySet();
		for (final Entry<String, BusNetwork> entry : entrySet) {
			final String cityName = entry.getKey();
			final BusNetwork bn = entry.getValue();
			final String sql = bn.parseStops();

			Assert.assertNotNull(sql);
			Assert.assertTrue(sql.length() > 0);
			postProcess(cityName + "_stops.sql", sql);
		}
	}

	@Test
	public void testStopTimes() throws SQLException, IOException {
		final Set<Entry<String, BusNetwork>> entrySet = testMap.entrySet();
		for (final Entry<String, BusNetwork> entry : entrySet) {
			final String cityName = entry.getKey();
			final BusNetwork bn = entry.getValue();
			final String sql = bn.parseStopTimes();

			Assert.assertNotNull(sql);
			Assert.assertTrue(sql.length() > 0);
			postProcess(cityName + "_stop_times.sql", sql);
		}
	}

	@Test
	public void testTrips() throws SQLException, IOException {
		final Set<Entry<String, BusNetwork>> entrySet = testMap.entrySet();
		for (final Entry<String, BusNetwork> entry : entrySet) {
			final String cityName = entry.getKey();
			final BusNetwork bn = entry.getValue();
			final String sql = bn.parseTrips();

			Assert.assertNotNull(sql);
			Assert.assertTrue(sql.length() > 0);
			postProcess(cityName + "_trips.sql", sql);
		}
	}

	// Private methods

	private void postProcess(final String filename, final String sql) throws SQLException, IOException {
		final String resultFolder = TestHelper.TEST_OUTPUT_SCRIPT + File.separatorChar + TestHelper.TEST_CITY + "_gtfs";
		ScriptGenerator.write2File(resultFolder + File.separatorChar + filename, sql);

		if (db != null && performInserts) {
			try {
				db.executeBatch(sql.split(";"));
			} catch (final SQLException e) {
				throw e;
			}
		}
	}

}
