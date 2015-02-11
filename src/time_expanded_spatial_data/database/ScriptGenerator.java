package time_expanded_spatial_data.database;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import com.sun.xml.internal.messaging.saaj.packaging.mime.util.OutputUtil;

import time_expanded_spatial_data.bus_network.ViewText;

public class ScriptGenerator {
	
	private static final String DROP_SCHEMA = "DROP SCHEMA IF EXISTS ";
	private static final String DROP_TABLE = "DROP TABLE IF EXISTS ";
	private static final String CREATE_SCHEMA = "CREATE SCHEMA ";
	private static final String CREATE_TABLE = "CREATE TABLE ";
	private static final String BLANK = " "; 
	private static final String SCHEMA_DIVIDER = ".";
	private static final String BLANK_LINE = "\n";
	private static final String INSERT = "INSERT INTO ";
	private static final String VALUES = " VALUES";
	
	private String schemaName;
	private HashMap<String, String> parameters;
	private HashMap<String, String> actions;
	private ArrayList<Table> tables;
	private String script;
	private BufferedWriter output;
	
	public ScriptGenerator(ArrayList<Table> tables, HashMap<String, String> params, String path){
		this.tables = tables;
		parameters = params;
		script = "";
		setWriter(path);
	}
	
	public ScriptGenerator(String path){
		setWriter(path);
	}
	
	public ScriptGenerator(){ 
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
	
	private String createPrimaryKey(Table t){
		String primary = "\tprimary key(";
		boolean comma = false;
		for(String s : t.getPrimaryKeys()){
			if(comma)
				primary += ", ";
			comma = true;
			primary += s;
		}
		primary += ")";
		return primary;
	}
	
	private String createForeignKey(Table t){
		String keys = "";
		boolean comma = false;
		for(String s : t.getForeignKeys()){
			String foreign1 = "\tforeign key(";
			String foreign2 = "references";
			if(comma)
				keys += ",\n";
			comma = true;
			StringTokenizer st = new StringTokenizer(s, ";");
			foreign1 += st.nextToken();
			foreign2 += BLANK + st.nextToken() + SCHEMA_DIVIDER + st.nextToken() + "(" + st.nextToken();
			foreign1 += ")";
			foreign2 += ")";
			keys += foreign1 + BLANK + foreign2;
//			System.out.println("keys: "+ keys);
		}
		return keys;
	}
	
	public String createTable(Table t){
		String s = CREATE_TABLE;
		s += t.getSchemaName() + SCHEMA_DIVIDER + t.getTableName() + "(\n";
		boolean comma = false;
		for(String attr : t.getAttributes().keySet()){
			if(!attr.equals("")){
				if(comma)
					s += ",\n";
				comma = true;
				s += "\t" + attr + BLANK + t.getAttributes().get(attr);
			}
		}
		s += ",\n";
		if(t.getPrimaryKeys().size() != 0){
			s += createPrimaryKey(t)  ;
			if(t.getForeignKeys().size() != 0)
				s += "," + BLANK_LINE + createForeignKey(t);
		}
		s += BLANK_LINE;
		s += ");\n" + BLANK_LINE;
		return s;
	}
	
	public String dropTable(Table t){
		return DROP_TABLE + BLANK + t.getSchemaName() + SCHEMA_DIVIDER + t.getTableName() + " CASCADE;" + BLANK_LINE;
	}
	
	public void createScript(){
		String script = "";
//		script += dropSchema(true) + BLANK_LINE ;
//		script += createSchema() + BLANK_LINE;
		for(Table t : tables)
			script += dropTable(t);
		script += BLANK_LINE;
		for(Table t : tables)
			script += createTable(t);
		this.script = script;
	}
	
	public void createBusScript(String city, String text){
		String script = "";
		String aux = text.replace("<city>", city); 
		script += aux + BLANK_LINE;
		this.script = script;
	}
	
	public void setWriter(String path){
		try {
			output = new BufferedWriter(new FileWriter(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeScipt(){
		try {
			output.write(script);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void openWriter(String path, boolean append){
		try {
			output = new BufferedWriter(new FileWriter(path, append));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	public void closeWriter(){
		try {
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void insert(String table, List<String> fields, List<String> values, int nrFields){
		String script = "";
		boolean comma = false;
		int j = 0;
		String fieldsPart = "";
		if(fields != null){
			fieldsPart += "(";
			for(String s : fields){
				if(comma)
					s += ", ";
				comma = true;
				fieldsPart += s;
			}
			fieldsPart += ")" + BLANK;
		}
		
		for(int i = 0; i < (values.size()/nrFields); i++){
			comma = false;
			script += INSERT + table + BLANK;
			script += fieldsPart;
			script += VALUES + "(";
			for(; (j % nrFields) != nrFields; j++){
				String s = values.get(j);
				if(comma)
					s += ", ";
				comma = true;
				script += s;
			}
			script += ")";
			if(j < values.size()-1)
				script += ",\n";
		}
		this.script = script;
	}
	
	public void createByReplace(String source, String replaceS, String replaceD){
		this.script = source.replaceAll(replaceS, replaceD);
	}
	
}
