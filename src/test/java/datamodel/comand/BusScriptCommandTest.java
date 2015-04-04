package datamodel.comand;

import datamodel.TestHelper;
import datamodel.command.BusScriptCommand;
import datamodel.command.ICommand;

import java.io.File;
import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.Test;

public class BusScriptCommandTest {

	private static final String TEST_PREFIX = TestHelper.TEST_OUTPUT_SCRIPT + File.separatorChar + TestHelper.TEST_CITY;
	private static final String[] EXPECTED_FILENAMES = new String[] {
		TEST_PREFIX + "_bus_network.sql",
		TEST_PREFIX + "_bus_nodes_edges.sql",
		TEST_PREFIX + "_bus_trip_schedule.sql"
	};

	@Test
	public void testCommand() {
		final ICommand cmd = new BusScriptCommand(TestHelper.TEST_OUTPUT_SCRIPT, TestHelper.TEST_CITY);
		cmd.execute();

		Arrays.stream(EXPECTED_FILENAMES).forEach(f ->
			Assert.assertTrue(new File(f).exists())
		);
	}

}
