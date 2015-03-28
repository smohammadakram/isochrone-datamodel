package datamodel.command;

import datamodel.database.Table;

import java.util.Collection;

import org.testng.Assert;
import org.testng.annotations.Test;

public class GenerateTableExpTest {
	private static final int TABLES_NR_EXPECTED = 2;

	@Test
	public void testAllTables() {
		final GenerateTableExp d = new GenerateTableExp("test", "testCity");
		final Collection<Table> tables = d.getAllTables();

		Assert.assertNotNull(tables);
		Assert.assertEquals(tables.size(), TABLES_NR_EXPECTED);
	}

}
