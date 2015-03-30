CREATE VIEW time_expanded.<city>_edges_to_merge AS
	SELECT b1.edge_id as source_edge_id, b1.edge_source as source, b1.edge_length as length_source, b1.edge_geometry source_geometry,
		b2.edge_id as dest_edge_id, b2.edge_destination as dest, b2.edge_length as dest_length, b2.edge_geometry as dest_geometry,
		bn.node_id, bn.node_in_degree, bn.node_out_degree
	FROM time_expanded.<city>_street_edges b1, time_expanded.<city>_street_edges b2, time_expanded.<city>_bus_ped_network bn
	WHERE b1.edge_destination = b2.edge_source AND b1.edge_source != b2.edge_destination AND b1.edge_destination = bn.node_id
		AND bn.node_in_degree = '1' AND bn.node_out_degree = '1';
