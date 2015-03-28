package datamodel.timeexpanded.streetnetwork;

public final class StreetSQL {

	public static final String BUILD_STREET_NETWORK = "DROP TABLE IF EXISTS  time_expanded.<city>_street_nodes CASCADE;\n"
		+ "DROP TABLE IF EXISTS  time_expanded.<city>_street_edges CASCADE;\n\n"
		+ "CREATE TABLE time_expanded.<city>_street_nodes(\n"
		+ "\tnode_in_degree integer,\n"
		+ "\tnode_out_degree integer,\n"
		+ "\tnode_id bigint,\n"
		+ "\tnode_geometry geometry not null,\n"
		+ "\tprimary key(node_id)\n"
		+ ");\n\n"
		+ "CREATE TABLE time_expanded.<city>_street_edges(\n"
		+ "\tedge_geometry geometry,\n"
		+ "\tedge_source bigint not null,\n"
		+ "\tedge_id bigserial not null,\n"
		+ "\tedge_destination bigint not null,\n"
		+ "\tedge_length double precision,\n"
		+ "\tprimary key(edge_id),\n"
		+ "\tforeign key(edge_source) references time_expanded.<city>_street_nodes(node_id),\n"
		+ "\tforeign key(edge_destination) references time_expanded.<city>_street_nodes(node_id)\n"
		+ ");";

}
