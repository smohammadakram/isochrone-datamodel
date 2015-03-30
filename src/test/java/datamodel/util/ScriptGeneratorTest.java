package datamodel.util;

import datamodel.impl.ScriptGenerator;

import java.io.File;
import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ScriptGeneratorTest {
	private static final String CITY = "ttt";
	private static final String PATH = "build/tmp/scripts";

	@BeforeClass
	public void setup() {
		final File f = new File(PATH);
		if (!f.exists() && !f.mkdirs()) {
			Assert.fail("Could not generate output directory");
		}
	}

	@Test
	public void testByReplace() throws IOException {
		final String testResource = "bus_network.sql";

		final String output = PATH + File.separatorChar + CITY + "_" + testResource;
		final ScriptGenerator sg = new ScriptGenerator(testResource, "<city>", CITY);
		sg.writeScript(output);

		Assert.assertTrue((new File(output)).exists());
	}

}
