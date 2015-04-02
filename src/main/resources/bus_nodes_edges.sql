DELETE FROM time_expanded.<city>_bus_routes;
DELETE FROM time_expanded.<city>_bus_nodes;
DELETE FROM time_expanded.<city>_bus_edges;

INSERT INTO time_expanded.<city>_bus_routes(route_id, route_descr_long, route_descr_short)
	SELECT r.route_id, r.long_name, r.short_name
	FROM time_expanded.tmp_routes  r;

INSERT INTO time_expanded.<city>_bus_nodes(node_route_id, node_geometry)
	SELECT DISTINCT rs.route_id, ST_AsEWKT(rs.stop_geom)
	FROM time_expanded.tmp_route_and_stop rs;

CREATE OR REPLACE VIEW time_expanded.<city>_node_couple AS
	SELECT n1.node_id AS source, n2.node_id AS target, n1.node_route_id AS source_route,
	n2.node_route_id AS target_route, n1.node_geometry AS source_geom, n2.node_geometry AS target_geometry
	FROM time_expanded.<city>_bus_nodes n1
		JOIN time_expanded.<city>_bus_nodes n2 ON n1.node_route_id = n2.node_route_id;

INSERT INTO time_expanded.<city>_bus_edges(edge_source, edge_destination, edge_route_id)
	SELECT nc.source, nc.target, nc.source_route
	FROM  time_expanded.<city>_node_couple nc, time_expanded.tmp_bus_edges be
	WHERE nc.source_geom = be.source_geom AND nc.target_geometry = be.target_geom
		AND nc.source_route = be.route_id AND nc.target_route = be.r_id;

CREATE OR REPLACE VIEW time_expanded.<city>_bus_edges_coord AS
	SELECT e.edge_id, e.edge_source, n1.node_geometry AS source_geom, e.edge_destination, n2.node_geometry AS dest_geom, e.edge_route_id
	FROM (time_expanded.<city>_bus_edges e JOIN time_expanded.<city>_bus_nodes n1 ON e.edge_source = n1.node_id)
		JOIN time_expanded.<city>_bus_nodes n2 ON e.edge_destination = n2.node_id;

CREATE OR REPLACE VIEW time_expanded.<city>_bus_trips_tmp AS
	SELECT DISTINCT bn1.t_id AS trip_id, bec.edge_id, bec.edge_route_id, bn1.departure_time, bn1.arrival_time, bn1.service_id, bn1.edge_sequence
	FROM time_expanded.tmp_trip_edges bn1, time_expanded.<city>_bus_edges_coord bec
	WHERE bn1.route_id = bec.edge_route_id AND bn1.source_geom = bec.source_geom AND bn1.destination_geom = bec.dest_geom;
