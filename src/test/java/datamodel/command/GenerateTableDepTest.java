package datamodel.command;

import datamodel.database.Table;

import java.util.Collection;

import org.testng.Assert;
import org.testng.annotations.Test;

public class GenerateTableDepTest {
	private static final int TABLES_NR_EXPECTED = 5;

	@Test
	public void testAllTables() {
		final GenerateTableDep d = new GenerateTableDep("test", "testCity");
		final Collection<Table> tables = d.getAllTables();

		Assert.assertNotNull(tables);
		Assert.assertEquals(tables.size(), TABLES_NR_EXPECTED);
	}

}
