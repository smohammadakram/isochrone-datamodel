CREATE OR REPLACE VIEW time_expanded.<city>_node_couple AS
	SELECT n1.node_id AS source, n2.node_id AS target, n1.node_route_id AS source_route,
	n2.node_route_id AS target_route, n1.node_geometry AS source_geom, n2.node_geometry AS target_geometry
	FROM time_expanded.<city>_bus_nodes n1
		JOIN time_expanded.<city>_bus_nodes n2 ON n1.node_route_id = n2.node_route_id;
