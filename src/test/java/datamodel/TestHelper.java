package datamodel;

import java.io.File;

import org.testng.Assert;

public final class TestHelper {
	public static final String TEST_CITY = "ttt";
	public static final String TEST_GTFS = "src/test/resources/gtfs";
	public static final String TEST_OUTPUT = "build/tmp/scripts";

	private TestHelper() { }

	public static void createTestOutputDirectory() {
		final File f = new File(TEST_OUTPUT);
		if (!f.exists() && !f.mkdirs()) {
			Assert.fail("Could not generate output directory");
		}
	}

}
