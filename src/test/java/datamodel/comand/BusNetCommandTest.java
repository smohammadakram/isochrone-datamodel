package datamodel.comand;

import datamodel.TestHelper;
import datamodel.command.BusNetCommand;
import datamodel.command.ICommand;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class BusNetCommandTest {

	@BeforeClass
	public void setup() {
		// Creates database tables (or truncates existing ones by dropping and re-creating)
		Assert.assertTrue(TestHelper.executeScript("bus_network.sql"), "Setup failed");
	}

	@Test
	public void testCommand() {
		final ICommand cmd = new BusNetCommand(TestHelper.TEST_GTFS, TestHelper.TEST_CITY);
		cmd.execute();
	}

}
