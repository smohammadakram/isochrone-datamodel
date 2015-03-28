package datamodel.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

public class ScriptGenerator {
	private static final Charset FILE_CS = Charset.forName("UTF-8");
	private String schemaName;
	private String script;

	// Constructor

	public ScriptGenerator(final File f, final String replaceS, final String replaceD) throws IOException {
		this.script = readFromFile(f).replaceAll(replaceS, replaceD);
	}

	// Getter

	public String getScript() {
		return (script == null) ? "" : script;
	}

	// Setter

	public String getSchemaName() {
		return schemaName;
	}

	// Public methods

	public void writeScript(final String path) throws IOException {
		writeScript(new File(path));
	}

	public void writeScript(final File f) throws IOException {
		if (script == null) {
			return;
		}

		final File p = f.getParentFile();
		if (!p.exists() && !p.mkdirs()) {
			throw new IOException("Could not generate output directory");
		}

		try (final Writer output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), FILE_CS))) {
			output.write(script);
		}
	}

	// Private static methods

	private static String readFromFile(final File f) throws IOException {
		final StringBuilder sb = new StringBuilder();

		try (BufferedReader r = new BufferedReader(new FileReader(f))) {
			String s = null;
			while ((s = r.readLine()) != null) {
				sb.append(s);
			}
		}

		return sb.toString();
	}

}
