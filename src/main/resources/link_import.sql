DELETE FROM time_expanded.<city>_link;
INSERT INTO time_expanded.<city>_link (link_source, link_source_mode, link_destination, link_destination_mode)
	SELECT DISTINCT bn.node_id, bn.node_mode, pn.node_id, pn.node_mode
	FROM time_expanded.<city>_bus_nodes bn,
		time_expanded.<city>_pedestrian_nodes pn,
		time_expanded.<city>_bus_to_ped_coords_interpolate i
	WHERE bn.node_geometry = i.bus_node_geometry AND st_line_interpolate_point = pn.node_geometry;

INSERT INTO time_expanded.<city>_link (link_source, link_source_mode, link_destination, link_destination_mode)
	SELECT DISTINCT pn.node_id, pn.node_mode, bn.node_id, bn.node_mode
	FROM time_expanded.<city>_bus_nodes bn,
		time_expanded.<city>_street_nodes pn,
		time_expanded.<city>_bus_to_ped_coords_interpolate i
	WHERE bn.node_geometry = i.bus_node_geometry AND st_line_interpolate_point = pn.node_geometry;