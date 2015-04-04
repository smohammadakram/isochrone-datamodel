DROP TABLE IF EXISTS time_expanded.<city>_bus_calendar CASCADE;
DROP TABLE IF EXISTS time_expanded.<city>_bus_nodes CASCADE;
DROP TABLE IF EXISTS time_expanded.<city>_bus_routes CASCADE;
DROP TABLE IF EXISTS time_expanded.<city>_bus_edges CASCADE;
DROP TABLE IF EXISTS time_expanded.<city>_link CASCADE;
DROP TABLE IF EXISTS time_expanded.<city>_trip_schedule CASCADE;

CREATE TABLE time_expanded.<city>_bus_calendar (
	service_id varchar(32) NOT NULL,
	service_start_date text NOT NULL,
	service_end_date text NOT NULL,
	service_vector varchar(366) NOT NULL,
	PRIMARY KEY (service_id, service_start_date, service_end_date)
);

CREATE TABLE time_expanded.<city>_bus_routes (
	route_id varchar(32) NOT NULL,
	route_descr_long varchar(64) NOT NULL,
	route_descr_short varchar(16) NOT NULL,
	PRIMARY KEY (route_id)
);

CREATE TABLE time_expanded.<city>_bus_nodes (
	node_id serial NOT NULL,
	node_geometry geometry NOT NULL,
	node_in_degree integer,
	node_out_degree integer,
	node_route_id varchar(32) NOT NULL,
	PRIMARY KEY (node_id),
	FOREIGN KEY (node_route_id) REFERENCES time_expanded.<city>_bus_routes(route_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE time_expanded.<city>_bus_edges (
	edge_id serial NOT NULL,
	edge_source integer NOT NULL,
	edge_destination integer NOT NULL,
	edge_route_id varchar(32) NOT NULL,
	PRIMARY KEY (edge_id),
	FOREIGN KEY (edge_source) REFERENCES time_expanded.<city>_bus_nodes(node_id) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY (edge_destination) REFERENCES time_expanded.<city>_bus_nodes(node_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE time_expanded.<city>_trip_schedule (
	trip_id varchar(32) NOT NULL,
	trip_edge integer NOT NULL,
	trip_route_id varchar(32) NOT NULL,
	trip_time_d text NOT NULL,
	trip_time_a text NOT NULL,
	trip_service varchar(32) NOT NULL,
	trip_service_s_date text NOT NULL,
	trip_service_e_date text NOT NULL,
	trip_seq_nr integer NOT NULL DEFAULT -1,
	PRIMARY KEY (trip_id, trip_edge, trip_route_id, trip_time_d, trip_time_a, trip_service, trip_service_s_date, trip_service_e_date, trip_seq_nr),
	FOREIGN KEY (trip_edge) REFERENCES time_expanded.<city>_bus_edges(edge_id) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY (trip_service, trip_service_s_date, trip_service_e_date) REFERENCES time_expanded.<city>_bus_calendar(service_id, service_start_date, service_end_date) ON DELETE NO ACTION ON UPDATE NO ACTION,
	FOREIGN KEY (trip_route_id) REFERENCES time_expanded.<city>_bus_routes(route_id) ON DELETE CASCADE ON UPDATE CASCADE
);
