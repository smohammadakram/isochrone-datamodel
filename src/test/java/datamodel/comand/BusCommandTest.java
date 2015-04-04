package datamodel.comand;

import datamodel.TestHelper;
import datamodel.command.BusCommand;
import datamodel.command.ICommand;

import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.Test;

public class BusCommandTest {

	private static final String[] EXPECTED_TABLES = new String[] {
		TestHelper.TEST_CITY + "_bus_calendar",
		TestHelper.TEST_CITY + "_bus_edges",
		TestHelper.TEST_CITY + "_bus_nodes",
		TestHelper.TEST_CITY + "_bus_routes",
		TestHelper.TEST_CITY + "_trip_schedule"
	};

	@Test
	public void testCommand() {
		final ICommand cmd = new BusCommand(TestHelper.TEST_OUTPUT_SCRIPT, TestHelper.TEST_GTFS, TestHelper.TEST_CITY);
		cmd.execute();

		Arrays.stream(EXPECTED_TABLES).forEach(t -> {
			Assert.assertTrue(TestHelper.dbTableExists(t), "Expected table \"" + t + "\" was not found");
			Assert.assertNotEquals(TestHelper.dbTableCount(t), 0, "Table \"" + t + "\" does not contain any data");
		});
	}

}
