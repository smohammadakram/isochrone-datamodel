package datamodel.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

public final class ScriptGenerator {
	private static final Charset FILE_CS = Charset.forName("UTF-8");
	private String script;

	// Constructor

	public ScriptGenerator(final String file, final String replaceS, final String replaceD) throws IOException {
		this.script = readFromFile(file).replaceAll(replaceS, replaceD);
	}

	// Getter

	public String getScript() {
		return (script == null) ? "" : script;
	}

	// Public methods

	private String readFromFile(final String file) throws IOException {
		final StringBuilder sb = new StringBuilder();

		try (final BufferedReader r = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(File.separatorChar + file), FILE_CS))) {
			String s = null;
			while ((s = r.readLine()) != null) {
				sb.append(s + "\n");
			}
		}

		return sb.toString();
	}

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

}
