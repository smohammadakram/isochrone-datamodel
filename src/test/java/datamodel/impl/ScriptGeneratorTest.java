package datamodel.impl;

import datamodel.TestHelper;

import java.io.File;
import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ScriptGeneratorTest {

	@BeforeClass
	public void setup() {
		TestHelper.createTestOutputDirectory();
	}

	@Test
	public void testByReplace() throws IOException {
		final String testResource = "bus_network.sql";

		final String output = TestHelper.TEST_OUTPUT + File.separatorChar + TestHelper.TEST_CITY + "_" + testResource;
		final ScriptGenerator sg = new ScriptGenerator(testResource, "<city>", TestHelper.TEST_CITY);
		sg.writeScript(output);

		Assert.assertTrue((new File(output)).exists());
	}

}
