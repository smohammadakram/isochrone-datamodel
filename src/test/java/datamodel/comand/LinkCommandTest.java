package datamodel.comand;

import datamodel.TestHelper;
import datamodel.command.ICommand;
import datamodel.command.LinkCommand;

import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class LinkCommandTest {
	private static final String[] EXPECTED_TABLES = new String[] {
		TestHelper.TEST_CITY + "_links"
	};

	@BeforeClass
	public void setup() {
		new BusCommandTest().testCommand();
		new StreetCommandTest().testCommand();
	}

	@Test
	public void testCommand() {
		final ICommand cmd = new LinkCommand(TestHelper.TEST_OUTPUT_SCRIPT, TestHelper.TEST_CITY);
		cmd.execute();

		Arrays.stream(EXPECTED_TABLES).forEach(t -> {
			Assert.assertTrue(TestHelper.dbTableExists(t), "Expected table \"" + t + "\" was not found");
			Assert.assertNotEquals(TestHelper.dbTableCount(t), 0, "Table \"" + t + "\" does not contain any data");
		});
	}

}
