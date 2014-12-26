package time_expanded_spatial_data.database;

import java.util.ArrayList;
import java.util.HashMap;

public class Table {
	
	/**
	 * The name of the schema this table belongs to.
	 */
	private String schemaName;
	
	/**
	 * The name of the table to store.
	 */
	private String tableName;
	
	/**
	 * List of primary keys.
	 */
	private ArrayList<String> primaryKeys;
	
	/**
	 * List of foreign keys.
	 */
	private ArrayList<String> foreignKeys;
	
	/**
	 * The list of attributes where the keys store the name and the value are the data types.
	 */
	private HashMap<String, String> attributes;
	
	public Table(String tableName, ArrayList<String> primaryKeys, ArrayList<String> foreignKeys, HashMap<String, String> attributes) {
		this.tableName = tableName;
		this.primaryKeys = primaryKeys;
		this.foreignKeys = foreignKeys;
		this.attributes = attributes;
	}
	

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	/**
	 * Return the name of this table.
	 * @return String
	 */
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public ArrayList<String> getPrimaryKeys() {
		return primaryKeys;
	}

	public void setPrimaryKeys(ArrayList<String> primaryKeys) {
		this.primaryKeys = primaryKeys;
	}

	public ArrayList<String> getForeignKeys() {
		return foreignKeys;
	}

	public void setForeignKeys(ArrayList<String> foreignKeys) {
		this.foreignKeys = foreignKeys;
	}

	public HashMap<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(HashMap<String, String> attributes) {
		this.attributes = attributes;
	}

}
