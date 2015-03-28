package data_model.time_expanded.database;

/**
 * Class containing the attributes for tables with data type.
 */
public  final class TimeExpTablesDescription {

	public static final String SCHEMA_NAME = "time_expanded";

	/**
	 * Definition of attributes for street nodes table.
	 */
	public static final class StreetNodes{
		public static final String NODE_ID = "bigserial";
		public static final String NODE_MODE = "integer default 0 not null";
		public static final String NODE_GEOMETRY = "geometry not null";
		public static final String NODE_IN_DEGREE = "integer";
		public static final String NODE_OUT_DEGREE = "integer";
	}

	/**
	 * Definition of attributes of street edges table.
	 */
	public static final class StreetEdges{
		public static final String EDGE_ID = "bigserial not null";
		public static final String EDGE_SOURCE = "bigint not null";
		public static final String EDGE_DESTINATION = "bigint not null";
		public static final String EDGE_LENGTH ="numeric(16,12) default 0 not null";
		public static final String EDGE_GEOMETRY = "geometry";
	}

}
