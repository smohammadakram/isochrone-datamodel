INSERT INTO time_expanded.<city>_trip_schedule
	SELECT DISTINCT te.t_id, bec.edge_id, bec.edge_route_id, te.departure_time, te.arrival_time, te.service_id, bc.service_start_date, bc.service_end_date, te.edge_sequence
	FROM time_expanded.tmp_trip_edges te
	JOIN time_expanded.<city>_bus_calendar bc ON te.service_id = bc.service_id
	JOIN time_expanded.<city>_bus_edges_coord bec ON te.route_id = bec.edge_route_id AND ST_Equals(te.source_geom, bec.source_geom) AND ST_Equals(te.destination_geom, bec.dest_geom);
