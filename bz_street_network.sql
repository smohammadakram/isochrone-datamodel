DROP SCHEMA IF EXISTS null CASCADE;

CREATE SCHEMA null;

CREATE TABLE null.bzstreet_nodes(
node_in_degree node_in_degree integer,
node_out_degree node_out_degree integer,
node_id node_id bigserial,
node_geometry node_geometry geometry not nullnull,
null,
);

CREATE TABLE null.bzstreet_edges(
edge_geometry edge_geometry geometry,
edge_source edge_source bigint not null,
edge_id edge_id bigserial not null,
edge_destination edge_destiantion bigint not nullnull,
null,
);

