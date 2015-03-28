package data_model.time_expanded.database;

import data_model.database.DBConnector;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class ScriptGenerator {

	private static final String BLANK = " ";
	private static final String BLANK_LINE = "\n";
	private static final String CREATE_SCHEMA = "CREATE SCHEMA ";
	private static final String CREATE_TABLE = "CREATE TABLE ";
	private static final String DROP_SCHEMA = "DROP SCHEMA IF EXISTS ";
	private static final String DROP_TABLE = "DROP TABLE IF EXISTS ";
	private static final String INSERT = "INSERT INTO ";
	private static final String SCHEMA_DIVIDER = ".";
	private static final String VALUES = " VALUES";
	private Map<String, String> actions;
	private BufferedWriter output;
	private Map<String, String> parameters;
	private String schemaName;
	private String script;
	private List<Table> tables;

	// Constructor

	public ScriptGenerator() {
		script = "";
	}

	public ScriptGenerator(final List<Table> tables, final Map<String, String> params, final String path) {
		this.tables = tables;
		parameters = params;
		script = "";

		setWriter(path);
	}

	public ScriptGenerator(final String path) {
		setWriter(path);
	}

	// Getter

	public void closeWriter() {
		try {
			output.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public boolean createAndExecute(final String source, final String replaceS, final String replaceD) {
		boolean result = false;
		try {
			final Statement stmt = DBConnector.getConnection().createStatement();
			result = stmt.execute(source.replaceAll(replaceS, replaceD));
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public void createBusScript(final String city, final String text) {
		String script = "";
		final String aux = text.replace("<city>", city);
		script += aux + BLANK_LINE;
		this.script = script;
	}

	public void createByReplace(final String source, final String replaceS, final String replaceD) {
		this.script = source.replaceAll(replaceS, replaceD);
	}

	public void createScript() {
		String script = "";
		// TODO: Why is this commented?
//		script += dropSchema(true) + BLANK_LINE ;
//		script += createSchema() + BLANK_LINE;
		for (final Table t : tables) {
			script += dropTable(t);
		}
		script += BLANK_LINE;
		for (final Table t : tables) {
			script += createTable(t);
		}
		this.script = script;
	}

	public String createTable(final Table t) {
		String s = CREATE_TABLE;
		s += t.getSchemaName() + SCHEMA_DIVIDER + t.getTableName() + "(\n";
		boolean comma = false;
		for (final String attr : t.getAttributes().keySet()) {
			if (!attr.equals("")) {
				if (comma) {
					s += ",\n";
				}
				comma = true;
				s += "\t" + attr + BLANK + t.getAttributes().get(attr);
			}
		}
		s += ",\n";
		if (t.getPrimaryKeys().size() != 0) {
			s += createPrimaryKey(t);
			if (t.getForeignKeys().size() != 0) {
				s += "," + BLANK_LINE + createForeignKey(t);
			}
		}
		s += BLANK_LINE;
		s += ");\n" + BLANK_LINE;
		return s;
	}

	public String dropTable(final Table t) {
		return DROP_TABLE + BLANK + t.getSchemaName() + SCHEMA_DIVIDER + t.getTableName() + " CASCADE;" + BLANK_LINE;
	}

	public Map<String, String> getActions() {
		return actions;
	}

	public String getParameterAtKey(final String key) {
		return parameters.get(key);
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	// Setter

	public String getSchemaName() {
		return schemaName;
	}

	// Public methods

	public String getScript() {
		return script;
	}

	public List<Table> getTables() {
		return tables;
	}

	public void insert(final String table, final List<String> fields, final List<String> values, final int nrFields) {
		String script = "";
		boolean comma = false;
		int j = 0;
		String fieldsPart = "";
		if (fields != null) {
			fieldsPart += "(";
			for (String s : fields) {
				if (comma) {
					s += ", ";
				}
				comma = true;
				fieldsPart += s;
			}
			fieldsPart += ")" + BLANK;
		}

		for (int i = 0; i < (values.size() / nrFields); i++) {
			comma = false;
			script += INSERT + table + BLANK;
			script += fieldsPart;
			script += VALUES + "(";
			for (; (j % nrFields) != nrFields; j++) {
				String s = values.get(j);
				if (comma) {
					s += ", ";
				}
				comma = true;
				script += s;
			}
			script += ")";
			if (j < values.size() - 1) {
				script += ",\n";
			}
		}
		this.script = script;
	}

	public void openWriter(final String path, final boolean append) {
		try {
			output = new BufferedWriter(new FileWriter(path, append));
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public void setActions(final HashMap<String, String> actions) {
		this.actions = actions;
	}

	public void setParameters(final HashMap<String, String> parameters) {
		this.parameters = parameters;
	}

	public void setSchemaName(final String schemaName) {
		this.schemaName = schemaName;
	}

	public void setScript(final String script) {
		this.script = script;
	}

	public void setTables(final ArrayList<Table> tables) {
		this.tables = tables;
	}

	public void setWriter(final String path) {
		try {
			output = new BufferedWriter(new FileWriter(path));
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public void writeScipt() {
		try {
			output.write(script);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public void writeScipt(final String sql) {
		try {
			script = sql;
			output.write(script);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	// Private methods

	private String createForeignKey(final Table t) {
		String keys = "";
		boolean comma = false;
		for (final String s : t.getForeignKeys()) {
			String foreign1 = "\tforeign key(";
			String foreign2 = "references";
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
//			System.out.println("keys: "+ keys);
		}

		return keys;
	}

	private String createPrimaryKey(final Table t) {
		String primary = "\tprimary key(";
		boolean comma = false;
		for (final String s : t.getPrimaryKeys()) {
			if (comma) {
				primary += ", ";
			}
			comma = true;
			primary += s;
		}
		primary += ")";
		return primary;
	}

	private String createSchema() {
		return CREATE_SCHEMA + schemaName + ";\n";
	}

	private String dropSchema(final boolean cascade) {
		if (cascade) {
			return DROP_SCHEMA + schemaName + " CASCADE;\n";
		} else {
			return DROP_SCHEMA + schemaName + ";\n";
		}
	}

}
