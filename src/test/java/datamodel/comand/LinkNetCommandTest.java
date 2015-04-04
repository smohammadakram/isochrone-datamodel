package datamodel.comand;

import datamodel.TestHelper;
import datamodel.command.ICommand;
import datamodel.command.LinkNetCommand;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class LinkNetCommandTest {

	@BeforeClass
	public void setup() {
		Assert.assertTrue(TestHelper.executeScript("datamodel/impl/link/link_network.sql"), "Setup failed");
		new BusCommandTest().testCommand();
		new StreetCommandTest().testCommand();
	}

	@Test
	public void testCommand() {
		final ICommand cmd = new LinkNetCommand(TestHelper.TEST_CITY);
		cmd.execute();
	}

}
