package sasa_importer.database;

import java.util.ArrayList;
import java.util.HashMap;

public class ScriptGenerator {
	
	private static final String DROP_SCHEMA = "DROP SCHEMA IF EXISTS ";
	private static final String CREATE_SCHEMA = "CREATE SCHEMA ";
	private static final String CREATE_TABLE = "CREATE TABLE ";
	private static final String BLANK = " "; 
	private static final String SCHEMA_DIVIDER = ".";
	private static final String BLANK_LINE = "\n";
	
	private String schemaName;
	private HashMap<String, String> parameters;
	private HashMap<String, String> actions;
	private ArrayList<Table> tables;
	private String script;
	
	public ScriptGenerator(ArrayList<Table> tables, HashMap<String, String> params){
		this.tables = tables;
		parameters = params;
		script = "";
	}

	public HashMap<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(HashMap<String, String> parameters) {
		this.parameters = parameters;
	}
	
	public String getParameterAtKey(String key){
		return parameters.get(key);
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public HashMap<String, String> getActions() {
		return actions;
	}

	public void setActions(HashMap<String, String> actions) {
		this.actions = actions;
	}

	public ArrayList<Table> getTables() {
		return tables;
	}

	public void setTables(ArrayList<Table> tables) {
		this.tables = tables;
	}
		
	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	private String dropSchema(boolean cascade){
		if(cascade)
			return DROP_SCHEMA + schemaName + " CASCADE;\n";
		else
			return  DROP_SCHEMA + schemaName + ";\n";
	}
	
	private String createSchema(){
		return CREATE_SCHEMA + schemaName + ";\n";
	}
	
	private String createPrimaryKey(){
		return null;
	}
	
	private String createForeignKey(){
		return null;
	}
	
	public String createTable(Table t){
		String s = CREATE_TABLE;
		s += t.getSchemaName() + SCHEMA_DIVIDER + t.getTableName() + "(\n";
		boolean comma = false;
		for(String attr : t.getAttributes().keySet()){
			if(comma)
				s += ",\n";
			comma = true;
			s += attr + BLANK + t.getAttributes().get(attr);
		}
		s += createPrimaryKey() + "," + BLANK_LINE;
		s += createForeignKey() + "," + BLANK_LINE;
		s += ");\n" + BLANK_LINE;
		return s;
	}
	
	public void createScript(){
		String script = "";
		script += dropSchema(true) + BLANK_LINE ;
		script += createSchema() + BLANK_LINE;
		for(Table t : tables)
			script += createTable(t);
		this.script = script;
	}
}
