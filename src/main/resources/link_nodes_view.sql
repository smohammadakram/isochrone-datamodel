CREATE OR REPLACE VIEW time_expanded.<city>_bus_nodes_projection AS
	SELECT n.node_id, b.edge_id, MIN(ST_Distance(n.node_geometry, b.edge_geometry, true)) AS min_dist
	FROM time_expanded.<city>_street_edges b, time_expanded.<city>_bus_nodes n
	GROUP BY n.node_id, b.edge_id;

CREATE OR REPLACE VIEW time_expanded.<city>_bus_min_ped_dist AS
	SELECT n.node_id, MIN(ST_Distance(n.node_geometry, p.edge_geometry))
	FROM time_expanded.<city>_street_edges p, time_expanded.<city>_bus_nodes n
	GROUP BY n.node_id;

CREATE OR REPLACE VIEW time_expanded.<city>_bus_to_ped_proj AS
	SELECT pe.node_id, bnp.edge_id, pe.min
	FROM time_expanded.<city>_bus_min_ped_dist pe, time_expanded.<city>_bus_nodes_projection bnp
	WHERE pe.node_id = bnp.node_id AND pe.min = bnp.min_dist;

	CREATE OR REPLACE VIEW time_expanded.<city>_bus_to_ped_coords_locate AS
	SELECT DISTINCT bp.node_id,
		ST_Line_Locate_Point(be.edge_geometry, bn.node_geometry) AS the_geom_locate,
		bn.node_geometry AS bus_node_geometry, be.edge_source, be.edge_destination,
		be.edge_geometry,
		bp.edge_id
	FROM time_expanded.<city>_bus_nodes bn, time_expanded.<city>_bus_to_ped_proj bp,
		time_expanded.<city>_street_edges be
	WHERE bp.edge_id = be.edge_id AND bp.node_id = bn.node_id;

CREATE OR REPLACE VIEW time_expanded.<city>_bus_to_ped_coords_interpolate AS
	SELECT DISTINCT l.node_id, ST_Line_Interpolate_Point(l.edge_geometry, l.the_geom_locate),
		l.bus_node_geometry, l.edge_source,l.edge_destination, l.edge_geometry, l.edge_id
	FROM time_expanded.<city>_bus_to_ped_coords_locate l;
