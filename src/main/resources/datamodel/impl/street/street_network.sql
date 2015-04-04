DROP TABLE IF EXISTS  time_expanded.<city>_street_nodes CASCADE;
DROP TABLE IF EXISTS  time_expanded.<city>_street_edges CASCADE;

CREATE TABLE time_expanded.<city>_street_nodes(
	node_in_degree integer,
	node_out_degree integer,
	node_id bigint,
	node_geometry geometry not null,
	primary key(node_id)
);

CREATE TABLE time_expanded.<city>_street_edges(
	edge_geometry geometry,
	edge_source bigint not null,
	edge_id bigserial not null,
	edge_destination bigint not null,
	edge_length double precision,
	primary key(edge_id),
	foreign key(edge_source) references time_expanded.<city>_street_nodes(node_id),
	foreign key(edge_destination) references time_expanded.<city>_street_nodes(node_id)
);
