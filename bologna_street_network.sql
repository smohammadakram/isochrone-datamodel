DROP SCHEMA IF EXISTS isochrones_2014 CASCADE;

CREATE SCHEMA isochrones_2014;

CREATE TABLE isochrones_2014.bologna_street_nodes(
	node_in_degree integer,
	node_out_degree integer,
	node_id bigserial,
	node_geometry geometry not null,
	primary key(node_id)
);

CREATE TABLE isochrones_2014.bologna_street_edges(
	edge_geometry geometry,
	edge_source bigint not null,
	edge_id bigserial not null,
	edge_destination bigint not null,
	primary key(edge_id),
	foreign key(edge_source) references isochrones_2014.bologna_street_nodes(node_id)	foreign key(edge_source)edge_destination) references isochrones_2014.bologna_street_nodes(node_id) isochrones_2014.bologna_street_nodes(node_id)
);

