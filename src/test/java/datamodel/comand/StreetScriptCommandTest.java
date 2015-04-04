package datamodel.comand;

import datamodel.TestHelper;
import datamodel.command.ICommand;
import datamodel.command.StreetScriptCommand;

import java.io.File;
import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.Test;

public class StreetScriptCommandTest {

	private static final String TEST_PREFIX = TestHelper.TEST_OUTPUT_SCRIPT + File.separatorChar + TestHelper.TEST_CITY;
	private static final String[] EXPECTED_FILENAMES = new String[] {
		TEST_PREFIX + "_street_network.sql"
	};

	@Test
	public void testCommand() {
		final ICommand cmd = new StreetScriptCommand(TestHelper.TEST_OUTPUT_SCRIPT, TestHelper.TEST_CITY);
		cmd.execute();

		Arrays.stream(EXPECTED_FILENAMES).forEach(f ->
			Assert.assertTrue(new File(f).exists())
		);
	}

}
