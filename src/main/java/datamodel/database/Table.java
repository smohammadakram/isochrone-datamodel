package datamodel.database;

import java.util.List;
import java.util.Map;

public class Table {

	private Map<String, String> attributes;
	private List<String> foreignKeys;
	private List<String> primaryKeys;
	private String schemaName;
	private String tableName;

	// Constructor

	public Table(final String schemaName, final String tableName, final List<String> primaryKeys, final List<String> foreignKeys, final Map<String, String> attributes) {
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

}
