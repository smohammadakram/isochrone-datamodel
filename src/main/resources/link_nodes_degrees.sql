UPDATE time_expanded.<city>_street_nodes AS s SET node_in_degree = tmp.in_d
FROM (SELECT sn.node_id, count(edge_destination) AS in_d
	FROM time_expanded.<city>_street_nodes snJOIN time_expanded.<city>_street_edges se
		ON sn.node_id = se.edge_destination
	GROUP BY sn.node_id, se.edge_destination
	ORDER BY sn.node_id) AS tmp
WHERE s.node_id = tmp.node_id;

UPDATE time_expanded.<city>_street_nodes AS s SET node_out_degree = tmp.out_d
FROM (SELECT sn.node_id, count(edge_source) AS out_d
FROM time_expanded.<city>_street_nodes sn JOIN time_expanded.<city>_street_edges se
		ON sn.node_id = se.edge_source
	GROUP BY sn.node_id, se.edge_source
	ORDER BY sn.node_id) as tmp
WHERE s.node_id = tmp.node_id;
