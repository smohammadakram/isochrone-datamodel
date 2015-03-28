package datamodel.database;

import datamodel.command.GenerateTableDep;
import datamodel.timeexpanded.busnetwork.BusSQL;

import java.io.File;
import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ScriptGeneratorTest {
	private static final String CITY = "test";
	private static final String PATH = "build/tmp/scripts";

	@Test
	public void testByReplace() throws IOException {
		final String output = PATH + "/busNodes.sql";
		final ScriptGenerator sg = new ScriptGenerator(BusSQL.BUS_NODES_EDGES, "<city>", CITY);
		sg.writeScript(output);

		Assert.assertTrue((new File(output)).exists());
	}

	@Test
	public void testTables() throws IOException {
		final String output = PATH + "/depTables.sql";
		final GenerateTableDep d = new GenerateTableDep(PATH, CITY);
		final ScriptGenerator sg = new ScriptGenerator(d.getAllTables());
		sg.writeScript(output);

		Assert.assertTrue((new File(output)).exists());
	}
}
