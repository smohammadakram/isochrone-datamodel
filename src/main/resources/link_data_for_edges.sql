CREATE OR REPLACE VIEW time_expanded.<city>_data_for_edges_snap AS
	SELECT DISTINCT bc.edge_id, bc.edge_source, bc.edge_destination, pn.node_id AS bus_ped_node_id,
		pn.node_geometry, bc.edge_geometry
	FROM time_expanded.<city>_street_nodes pn, time_expanded.<city>_bus_to_ped_coords_interpolate bc
	WHERE bc.st_line_interpolate_point = pn.node_geometry AND pn.node_in_degree = '1' AND pn.node_out_degree = '1';
