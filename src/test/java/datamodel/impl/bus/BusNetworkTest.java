package datamodel.impl.bus;

import datamodel.TestHelper;
import datamodel.db.DbConnector;
import datamodel.impl.ScriptGenerator;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class BusNetworkTest {
	private boolean performInserts = false;
	private DbConnector db;
	private BusNetwork bn;
	private BusNetwork bnZip;

	@BeforeClass
	public void setup() {
		db = new DbConnector();
		bn = new BusNetwork(db, TestHelper.TEST_GTFS, TestHelper.TEST_CITY);
		bnZip = new BusNetwork(db, TestHelper.TEST_GTFS_ZIP, TestHelper.TEST_CITY);
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
		final String sql = bn.parseCalendar();

		Assert.assertNotNull(sql);
		Assert.assertTrue(sql.length() > 0);
		postProcess("calendar.sql", sql);
	}

	@Test
	public void testCalendarDates() throws SQLException, IOException {
		final String sql = bn.parseCalendarDates();

		Assert.assertNotNull(sql);
		Assert.assertTrue(sql.length() > 0);

		postProcess("calendar_dates.sql", sql);
	}

	@Test
	public void testRoutes() throws SQLException, IOException {
		final String sql = bn.parseRoutes();

		Assert.assertNotNull(sql);
		Assert.assertTrue(sql.length() > 0);

		postProcess("routes.sql", sql);
	}

	@Test
	public void testStops() throws SQLException, IOException {
		final String sql = bn.parseStops();

		Assert.assertNotNull(sql);
		Assert.assertTrue(sql.length() > 0);

		postProcess("stops.sql", sql);
	}

	@Test
	public void testStopTimes() throws SQLException, IOException {
		final String sql = bn.parseStopTimes();

		Assert.assertNotNull(sql);
		Assert.assertTrue(sql.length() > 0);

		postProcess("stop_times.sql", sql);
	}

	@Test
	public void testTrips() throws SQLException, IOException {
		final String sql = bn.parseTrips();

		Assert.assertNotNull(sql);
		Assert.assertTrue(sql.length() > 0);

		postProcess("trips.sql", sql);
	}

	@Test
	public void testZipCalendar() throws SQLException, IOException {
		final String sql = bnZip.parseCalendar();

		Assert.assertNotNull(sql);
		Assert.assertTrue(sql.length() > 0);
		postProcess("calendar_fromZip.sql", sql);
	}

	@Test
	public void testZipCalendarDates() throws SQLException, IOException {
		final String sql = bnZip.parseCalendarDates();

		Assert.assertNotNull(sql);
		Assert.assertTrue(sql.length() > 0);

		postProcess("calendar_dates_fromZip.sql", sql);
	}

	@Test
	public void testZipRoutes() throws SQLException, IOException {
		final String sql = bnZip.parseRoutes();

		Assert.assertNotNull(sql);
		Assert.assertTrue(sql.length() > 0);

		postProcess("routes_fromZip.sql", sql);
	}

	@Test
	public void testZipStops() throws SQLException, IOException {
		final String sql = bnZip.parseStops();

		Assert.assertNotNull(sql);
		Assert.assertTrue(sql.length() > 0);

		postProcess("stops_fromZip.sql", sql);
	}

	@Test
	public void testZipStopTimes() throws SQLException, IOException {
		final String sql = bnZip.parseStopTimes();

		Assert.assertNotNull(sql);
		Assert.assertTrue(sql.length() > 0);

		postProcess("stop_times_fromZip.sql", sql);
	}

	@Test
	public void testZipTrips() throws SQLException, IOException {
		final String sql = bnZip.parseTrips();

		Assert.assertNotNull(sql);
		Assert.assertTrue(sql.length() > 0);

		postProcess("trips_fromZip.sql", sql);
	}

	// Private methods

	private void postProcess(final String filename, final String sql) throws SQLException, IOException {
		final String resultFolder = TestHelper.TEST_OUTPUT + File.separatorChar + TestHelper.TEST_CITY + "_gtfs";
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
