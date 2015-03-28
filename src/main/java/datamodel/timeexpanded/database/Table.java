package datamodel.timeexpanded.database;

import java.util.List;
import java.util.Map;

public class Table {

	/**
	 * The list of attributes where the keys store the name and the value are the data types.
	 */
	private Map<String, String> attributes;

	/**
	 * List of foreign keys.
	 */
	private List<String> foreignKeys;

	/**
	 * List of primary keys.
	 */
	private List<String> primaryKeys;

	/**
	 * The name of the schema this table belongs to.
	 */
	private String schemaName;

	/**
	 * The name of the table to store.
	 */
	private String tableName;

	// Constructor

	public Table(final String tableName, final List<String> primaryKeys, final List<String> foreignKeys, final Map<String, String> attributes) {
		this.tableName = tableName;
		this.primaryKeys = primaryKeys;
		this.foreignKeys = foreignKeys;
		this.attributes = attributes;
	}

	// Getter

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public List<String> getForeignKeys() {
		return foreignKeys;
	}

	public List<String> getPrimaryKeys() {
		return primaryKeys;
	}

	public String getSchemaName() {
		return schemaName;
	}

	/**
	 * Gets the name of this table.
	 *
	 * @return the name of this table.
	 */
	public String getTableName() {
		return tableName;
	}

	// Setter

	public void setAttributes(final Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public void setForeignKeys(final List<String> foreignKeys) {
		this.foreignKeys = foreignKeys;
	}

	public void setPrimaryKeys(final List<String> primaryKeys) {
		this.primaryKeys = primaryKeys;
	}

	public void setSchemaName(final String schemaName) {
		this.schemaName = schemaName;
	}

	public void setTableName(final String tableName) {
		this.tableName = tableName;
	}

}
