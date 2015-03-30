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
	private DbConnector db;
	private BusNetwork bn;

	@BeforeClass
	public void setup() {
		db = new DbConnector();
		bn = new BusNetwork(db, TestHelper.TEST_GTFS, TestHelper.TEST_CITY);
	}

	@AfterClass
	public void tearDown() {
		if (db != null) {
			db.close();
		}
	}

	@Test
	public void testCalendar() throws SQLException, IOException {
		final String sql = bn.parseCalendar();

		Assert.assertNotNull(sql);
		Assert.assertTrue(sql.length() > 0);

		ScriptGenerator.write2File(TestHelper.TEST_OUTPUT + File.separatorChar + "gtfs_calendar.sql", sql);
	}

	@Test
	public void testCalendarDates() throws SQLException, IOException {
		final String sql = bn.parseCalendarDates();

		Assert.assertNotNull(sql);
		Assert.assertTrue(sql.length() > 0);

		ScriptGenerator.write2File(TestHelper.TEST_OUTPUT + File.separatorChar + "gtfs_calendar_dates.sql", sql);
	}

	@Test
	public void testRoutes() throws SQLException, IOException {
		final String sql = bn.parseRoutes();

		Assert.assertNotNull(sql);
		Assert.assertTrue(sql.length() > 0);

		ScriptGenerator.write2File(TestHelper.TEST_OUTPUT + File.separatorChar + "gtfs_routes.sql", sql);
	}

	@Test
	public void testStops() throws SQLException, IOException {
		final String sql = bn.parseStops();

		Assert.assertNotNull(sql);
		Assert.assertTrue(sql.length() > 0);

		ScriptGenerator.write2File(TestHelper.TEST_OUTPUT + File.separatorChar + "gtfs_stops.sql", sql);
	}

	@Test
	public void testStopTimes() throws SQLException, IOException {
		final String sql = bn.parseStopTimes();

		Assert.assertNotNull(sql);
		Assert.assertTrue(sql.length() > 0);

		ScriptGenerator.write2File(TestHelper.TEST_OUTPUT + File.separatorChar + "gtfs_stop_times.sql", sql);
	}

	@Test
	public void testTrips() throws SQLException, IOException {
		final String sql = bn.parseTrips();

		Assert.assertNotNull(sql);
		Assert.assertTrue(sql.length() > 0);

		ScriptGenerator.write2File(TestHelper.TEST_OUTPUT + File.separatorChar + "gtfs_trips.sql", sql);
	}

}
