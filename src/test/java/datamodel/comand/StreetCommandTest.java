package datamodel.comand;

import datamodel.TestHelper;
import datamodel.command.ICommand;
import datamodel.command.StreetCommand;

import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.Test;

public class StreetCommandTest {
	private static final String[] EXPECTED_TABLES = new String[] {
		TestHelper.TEST_CITY + "_street_edges",
		TestHelper.TEST_CITY + "_street_nodes"
	};

	@Test
	public void testCommand() {
		final ICommand cmd = new StreetCommand(TestHelper.TEST_OUTPUT_SCRIPT, TestHelper.TEST_CITY, TestHelper.TEST_PBF);
		cmd.execute();

		Arrays.stream(EXPECTED_TABLES).forEach(t -> {
			Assert.assertTrue(TestHelper.dbTableExists(t), "Expected table \"" + t + "\" was not found");
			Assert.assertNotEquals(TestHelper.dbTableCount(t), 0, "Table \"" + t + "\" does not contain any data");
		});
	}

}
