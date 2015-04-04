package datamodel;

import datamodel.db.DbConfiguration;
import datamodel.db.DbConnector;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.testng.Assert;

public final class TestHelper {
	public static final String TEST_CITY = "test";
	public static final String TEST_EXPORT_BUS = "src/test/resources/bus_export.sql";
	public static final String TEST_EXPORT_STREET = "src/test/resources/street_export.sql";
	public static final String TEST_GTFS = "src/test/resources/gtfs_mebo.zip";
	public static final String TEST_OUTPUT_ARCHIVE = "build/tmp/archives";
	public static final String TEST_OUTPUT_SCRIPT = "build/tmp/scripts";
	public static final String TEST_PBF = "src/test/resources/street_mebo.osm.pbf";
	public static final String TEST_VDV = "src/test/resources/vdv_mebo.zip";

	private TestHelper() { }

	public static void createTestOutputDirectory() {
		final File f = new File(TEST_OUTPUT_SCRIPT);
		if (!f.exists() && !f.mkdirs()) {
			Assert.fail("Could not generate output directory");
		}
	}

	@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
	public static boolean dbTableExists(final String tableName) {
		final String catalogName = DbConfiguration.getInstance().getDbCatalog();
		final String query = "SELECT EXISTS ( SELECT 1 FROM information_schema.tables WHERE table_catalog='" + catalogName + "' AND table_name='" + tableName + "')";

		boolean result = false;
		try (
			final DbConnector db = new DbConnector();
			final Statement stmt = db.getStatement();
			final ResultSet rs = stmt.executeQuery(query);
		) {
			if (rs.first()) {
				result = rs.getBoolean(1);
			}
		} catch (final SQLException e) {
			result = false;
		}

		return result;
	}

	@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
	public static int dbTableCount(final String tableName) {
		final String query = "SELECT COUNT(*) FROM '" + tableName + "'";

		int result = -1;
		try (
			final DbConnector db = new DbConnector();
			final Statement stmt = db.getStatement();
			final ResultSet rs = stmt.executeQuery(query);
		) {
			if (rs.first()) {
				result = rs.getInt(1);
			}
		} catch (final SQLException e) {
			result = -1;
		}

		return result;
	}

}
