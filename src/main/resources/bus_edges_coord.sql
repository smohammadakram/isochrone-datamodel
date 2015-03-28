CREATE OR REPLACE VIEW time_expanded.bus_edges_coord AS
	SELECT e.edge_id, e.edge_source, n1.node_geometry AS source_geom, e.edge_destination, n2.node_geometry AS dest_geom, e.edge_route_id, e.edge_mode
	FROM (time_expanded.bus_edges e JOIN time_expanded.bus_nodes n1 ON e.edge_source = n1.node_id)
		JOIN time_expanded.bus_nodes n2 ON e.edge_destination = n2.node_id;
