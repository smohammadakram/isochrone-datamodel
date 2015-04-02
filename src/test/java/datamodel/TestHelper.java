package datamodel;

import datamodel.db.DbConnector;
import datamodel.impl.ScriptGenerator;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.testng.Assert;

public final class TestHelper {
	public static final String TEST_CITY = "mebo";
	public static final String TEST_GTFS = "src/test/resources/gtfs_mebo.zip";
	public static final String TEST_OUTPUT_ARCHIVE = "build/tmp/archives";
	public static final String TEST_OUTPUT_SCRIPT = "build/tmp/scripts";
	public static final String TEST_VDV = "src/test/resources/vdv_mebo.zip";

	private TestHelper() { }

	public static void createTestOutputDirectory() {
		final File f = new File(TEST_OUTPUT_SCRIPT);
		if (!f.exists() && !f.mkdirs()) {
			Assert.fail("Could not generate output directory");
		}
	}

	public static boolean executeScript(final String string) {
		boolean result = false;
		try {
			final String script = new ScriptGenerator("bus_network.sql", "<city>", TestHelper.TEST_CITY).getScript();
			try (final DbConnector db = new DbConnector()) {
				db.executeBatch(script.split(";"));
				result = true;
			}
		} catch (final IOException | SQLException e) {
			result = false;
		}

		return result;
	}

}
