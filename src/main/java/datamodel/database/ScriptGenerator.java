package datamodel.database;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

public class ScriptGenerator {
	private static final String BLANK = " ";
	private static final String BLANK_LINE = "\n";
	private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ";
	private static final String DROP_TABLE = "DROP TABLE IF EXISTS ";
	private static final Charset FILE_CS = Charset.forName("UTF-8");
	private static final String FIELD_DIVIDER = ", ";
	private static final String SCHEMA_DIVIDER = ".";
	private String schemaName;
	private String script;

	// Constructor

	public ScriptGenerator(final Collection<Table> tables) {
		this.script = tables2Script(tables);
	}

	public ScriptGenerator(final String source, final String replaceS, final String replaceD) {
		this.script = source.replaceAll(replaceS, replaceD);
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

	private static String createForeignKeys(final Table t) {
		final Collection<String> fkKeys =  t.getForeignKeys();
		if (fkKeys == null || fkKeys.isEmpty()) {
			return "";
		}

		String keys = "";
		boolean comma = false;
		for (final String s : fkKeys) {
			String foreign1 = "\tFOREIGN KEY(";
			String foreign2 = "REFERENCES";
			if (comma) {
				keys += ",\n";
			}
			comma = true;
			final StringTokenizer st = new StringTokenizer(s, ";");
			foreign1 += st.nextToken();
			foreign2 += BLANK + st.nextToken() + SCHEMA_DIVIDER + st.nextToken() + "(" + st.nextToken();
			foreign1 += ")";
			foreign2 += ")";
			keys += foreign1 + BLANK + foreign2;
		}

		return keys;
	}

	private static String createPrimaryKeys(final Table t) {
		final Collection<String> pkKeys = t.getPrimaryKeys();
		if (pkKeys == null || pkKeys.isEmpty()) {
			return "";
		}

		return "\tPRIMARY KEY(" + pkKeys.stream().collect(Collectors.joining(FIELD_DIVIDER)) + ")";
	}

	private static String createTable(final Table t) {
		final StringBuilder sb = new StringBuilder(CREATE_TABLE);
		sb.append(t.getSchemaName() + SCHEMA_DIVIDER + t.getTableName() + " (\n");
		sb.append(t.getAttributes().entrySet().stream().map((e) -> "\t" + e.getKey() + BLANK + e.getValue().toUpperCase(Locale.ENGLISH)).collect(Collectors.joining(FIELD_DIVIDER + BLANK_LINE)));

		final String pks = createPrimaryKeys(t);
		if (!pks.isEmpty()) {
			sb.append(",\n");
			sb.append(pks);
		}

		final String fks = createForeignKeys(t);
		if (!fks.isEmpty()) {
			sb.append(",\n");
			sb.append(createForeignKeys(t));
		}

		sb.append("\n");
		sb.append(");\n" + BLANK_LINE);
		return sb.toString();
	}

	private static String dropTable(final Table t) {
		return DROP_TABLE + BLANK + t.getSchemaName() + SCHEMA_DIVIDER + t.getTableName() + " CASCADE;" + BLANK_LINE;
	}

	private static String tables2Script(final Collection<Table> tables) {
		final StringBuilder sb = new StringBuilder();
		tables.forEach(t -> sb.append(dropTable(t)));
		sb.append(BLANK_LINE);
		tables.forEach(t -> sb.append(createTable(t)));

		return sb.toString();
	}

}
