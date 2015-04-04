package datamodel.comand;

import datamodel.TestHelper;
import datamodel.command.ICommand;
import datamodel.command.Vdv2GtfsCommand;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.Test;

public class Vdv2GtfsCommandTest {

	@Test
	public void testCommandWithZip() {
		final String fileOutput = TestHelper.TEST_OUTPUT_ARCHIVE + File.separatorChar + TestHelper.TEST_CITY + ".zip";
		final ICommand cmd = new Vdv2GtfsCommand(TestHelper.TEST_VDV, fileOutput);
		cmd.execute();

		Assert.assertTrue(new File(fileOutput).exists());
	}

}
