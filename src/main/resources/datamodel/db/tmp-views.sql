CREATE OR REPLACE VIEW time_expanded.tmp_route_and_stop AS 
	SELECT DISTINCT st.stop_id, tr.route_id, ST_SetSRID(ST_Makepoint(s.stop_long, s.stop_lat), 4326) as stop_geom
	FROM time_expanded.tmp_trips tr, time_expanded.tmp_stop_times st, time_expanded.tmp_stops s
	WHERE tr.t_id = st.trip_id AND st.stop_id = s.stop_id
	ORDER BY st.stop_id;

CREATE OR REPLACE VIEW time_expanded.tmp_bus_network AS 
	SELECT t.t_id, t.route_id, st.stop_sequence, st.stop_id, st.arrival_time, st.departure_time, ST_SetSRID(ST_Point(s.stop_long::double precision, s.stop_lat::double precision), 4326) AS stop_geom
	FROM time_expanded.tmp_stop_times st, time_expanded.tmp_trips t, time_expanded.tmp_stops s
	WHERE s.stop_id = st.stop_id AND t.t_id = st.trip_id;

CREATE OR REPLACE VIEW time_expanded.tmp_trip_edges AS
	SELECT bnw1.t_id, bnw1.route_id, bnw1.stop_id AS stop_id_source, bnw1.departure_time,
		bnw1.stop_geom AS source_geom, bnw2.stop_id AS stop_id_destination, bnw2.arrival_time,
		bnw2.stop_geom AS destination_geom, bnw2.stop_sequence AS edge_sequence, tr.service_id 
	FROM (time_expanded.tmp_bus_network bnw1 JOIN time_expanded.tmp_bus_network bnw2 
		ON bnw1.t_id = bnw2.t_id) JOIN time_expanded.tmp_trips tr ON bnw1.t_id = tr.t_id
	WHERE bnw1.stop_sequence = (bnw2.stop_sequence - 1);

CREATE OR REPLACE VIEW time_expanded.tmp_bus_edges AS 
	SELECT DISTINCT b1.stop_id AS source_id, b1.route_id, b2.stop_id AS target_id, b2.route_id AS r_id, b1.stop_geom AS source_geom, b2.stop_geom AS target_geom
   	FROM time_expanded.tmp_bus_network b1, time_expanded.tmp_bus_network b2
  	WHERE b1.t_id = b2.t_id AND b2.stop_sequence = (b1.stop_sequence + 1)
  	ORDER BY b1.route_id;
