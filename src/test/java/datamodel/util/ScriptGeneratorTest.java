package datamodel.util;

import datamodel.util.ScriptGenerator;

import java.io.File;
import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ScriptGeneratorTest {
	private static final String CITY = "ttt";
	private static final String PATH = "build/tmp/scripts";

	@Test
	public void testByReplace() throws IOException {
		final String output = PATH + "/" + CITY + "_bus_network.sql";
		final ScriptGenerator sg = new ScriptGenerator(new File("src/main/resources/bus_network.sql"), "<city>", CITY);
		sg.writeScript(output);

		Assert.assertTrue((new File(output)).exists());
	}

}
