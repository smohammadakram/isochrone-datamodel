package sasa_importer.database;

/**
 * Class containing the attributes for tables with data type.
 * @author Luca
 *
 */
public  final class TablesDescription {
	
	/**
	 * Definition of attributes for street nodes.
	 * @author Luca
	 *
	 */
	public static final class StreetNodes{
		public static final String NODE_ID = "node_id bigserial not null";
		public static final String NODE_MODE = "node_mode integer default 0 not null";
		public static final String NODE_GEOMETRY = "node_geometry geometry not null";
		public static final String NODE_IN_DEGREE = "node_in_degree integer";
		public static final String NODE_OUT_DEGREE = "node_out_degree integer";
	}
	
	/**
	 * Definition of attrinbutes of street edges.
	 * @author Luca
	 *
	 */
	public static final class StreetEdges{
		public static final String EDGE_ID = "edge_id bigserial not null";
		public static final String EDGE_SOURCE = "edge_source bigint not null";
		public static final String EDGE_DESTINATION = "edge_destiantion bigint not null";
		public static final String EDGE_LENGTH ="edge_lenght numeric(16,12) default 0 not null";
		public static final String EDGE_GEOMETRY = "edge_geometry geometry";
	}

}
