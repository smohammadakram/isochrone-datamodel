package time_expanded_spatial_data.database;

public class TimeDepTablesDescription {
	
	public static final String SCHEMA_NAME = "time_dependent";
	
	public static final class Nodes {
		public static final String NODE_ID = "bigserial";
		public static final String NODE_TYPE = "varchar(3)";
		public static final String NODE_GEOMETRY = "geometry";
		public static final String NODE_MODE = "smallint";
		public static final String NODE_TARGET_INDEGREE = "smallint";
		public static final String NODE_TARGET_C_INDEGREE = "smallint";
		public static final String NODE_INDEGREE = "smallint";
		public static final String NODE_C_INDEGREE = "smallint";
		public static final String NODE_OUTDEGREE = "smallint";
		public static final String NODE_C_OUTDEGREE = "smallint";
	}
	
	public static final class Edges {
		public static final String EDGE_ID = "bigserial not null";
		public static final String EDGE_SOURCE = "bigint not null";
		public static final String EDGE_DESTINATION = "bigint not null";
		public static final String EDGE_LENGTH ="numeric(16,12) default 0 not null";
		public static final String EDGE_ROUTE_ID ="integer default 0 not null";
		public static final String EDGE_SOURCE_OUTDEGREE ="smallint";
		public static final String EDGE_SOURCE_C_OUTDEGREE ="smallint";
		public static final String EDGE_GEOMETRY = "geometry";
		public static final String EDGE_MODE ="smallint";
		public static final String EDGE_SOURCE_MODE ="smallint";
		public static final String EDGE_DESTINATION_MODE ="smallint";
		public static final String EDGE_SOURCE_X ="double precision";
		public static final String EDGE_SOURCE_Y ="double precision";
		public static final String EDGE_TARGET_INDEGREE ="smallint";
		public static final String EDGE_TARGET_C_INDEGREE ="smallint";
	}
	
	public static final class Routes {
		public static final String ROUTE_ID = "integer";
		public static final String ROUTE_SHORT_NAME = "text";
		public static final String ROUTE_LONG_NAME = "text";
		public static final String ROUTE_DESC = "text";
		public static final String ROUTE_TYPE = "text";
		public static final String ROUTE_AGENCY_ID = "text";
	}
	
	public static final class Trips  {
		public static final String TRIP_ID = "integer";
		public static final String TRIP_ROUTE_ID = "integer";
		public static final String TRIP_SOURCE = "integer";
		public static final String TRIP_TIME_D = "integer";
		public static final String TRIP_DESTINATION = "integer";
		public static final String TRIP_TIME_A = "integer";
		public static final String TRIP_SERVICE_ID = "integer";
		public static final String TRIP_EDGE_ID = "integer";
		public static final String TRIP_SOURCE_GEO = "geometry";
	}
	
	public static final class Calendar {
		public static final String CALENDAR_SERVICE_ID = "integer";
		public static final String CALENDAR_START_DATE = "date";
		public static final String CALENDAR_END_DATE = "date";
		public static final String CALENDAR_DAY = "boolean";
	}
	
}
