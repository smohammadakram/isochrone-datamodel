package datamodel.comand;

import datamodel.TestHelper;
import datamodel.command.ICommand;
import datamodel.command.StreetNetCommand;

import java.io.File;
import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class StreetNetCommandTest {
	private static final String TEST_PREFIX = TestHelper.TEST_OUTPUT_SCRIPT + File.separatorChar + TestHelper.TEST_CITY;
	private static final String[] EXPECTED_FILENAMES = new String[] {
		TEST_PREFIX + "_street_nodes_import.sql",
		TEST_PREFIX + "_street_edges_import.sql"
	};

	@BeforeClass
	public void setup() {
		// Creates database tables (or truncates existing ones by dropping and re-creating)
		Assert.assertTrue(TestHelper.executeScript("datamodel/impl/street/street_network.sql"), "Setup failed");
	}

	@Test
	public void testCommand() {
		final ICommand cmd = new StreetNetCommand(TestHelper.TEST_OUTPUT_SCRIPT, TestHelper.TEST_CITY, TestHelper.TEST_PBF);
		cmd.execute();

		Arrays.stream(EXPECTED_FILENAMES).forEach(f ->
			Assert.assertTrue(new File(f).exists())
		);
	}

}
