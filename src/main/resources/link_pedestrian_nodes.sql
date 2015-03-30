UPDATE time_expanded.<city>_pedestrian_nodes
SET node_in_degree = '1', node_out_degree = '1'
WHERE node_in_degree IS NULL AND node_out_degree IS NULL;
