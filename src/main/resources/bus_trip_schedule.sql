INSERT INTO time_expanded.<city>_trip_schedule(trip_id, trip_edge, trip_route_id, trip_time_d, trip_time_a, trip_service, trip_service_s_date, trip_service_e_date, trip_seq_nr)
	SELECT DISTINCT bn1.t_id, bec.edge_id, bec.edge_route_id, bn1.departure_time, bn1.arrival_time, bn1.service_id, bc.service_start_date, bc.service_end_date, bn1.edge_sequence
	FROM (vdv_gtfs_tmp.trip_edges bn1 JOIN time_expanded.<city>_bus_edges_coord bec ON bn1.route_id = bec.edge_route_id AND bn1.source_geom = bec.source_geom AND bn1.destination_geom = bec.dest_geom)
	JOIN time_expanded.<city>_bus_calendar bc ON bn1.service_id = bc.service_id;
