package datamodel.impl.bus;

import datamodel.db.DbConnector;

import java.sql.SQLException;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class BusNetworkTest {
	private static final String TEST_CITY = "ttt";
	private static final String TEST_FOLDER = "src/test/resources/gtfs";
	private DbConnector db;
	private BusNetwork bn;

	@BeforeClass
	public void setup() {
		db = new DbConnector();
		bn = new BusNetwork(db, TEST_FOLDER, TEST_CITY);
	}

	@AfterClass
	public void tearDown() {
		if (db != null) {
			db.close();
		};
	}

	@Test
	public void testCalendar() throws SQLException {
		final String sql = bn.parseCalendar();

		Assert.assertNotNull(sql);
		Assert.assertTrue(sql.length() > 0);
	}

	@Test
	public void testRoutes() throws SQLException {
		final String sql = bn.parseRoutes();

		Assert.assertNotNull(sql);
		Assert.assertTrue(sql.length() > 0);
	}

	@Test
	public void testStops() throws SQLException {
		final String sql = bn.parseStops();

		Assert.assertNotNull(sql);
		Assert.assertTrue(sql.length() > 0);
	}

	@Test
	public void testTrips() throws SQLException {
		final String sql = bn.parseTrips();

		Assert.assertNotNull(sql);
		Assert.assertTrue(sql.length() > 0);
	}

	@Test
	public void testTripSequences() throws SQLException {
		final String sql = bn.parseTripSequence();

		Assert.assertNotNull(sql);
		Assert.assertTrue(sql.length() > 0);
	}

}
