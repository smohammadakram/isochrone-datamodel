package time_expanded_spatial_data.street_network;

public final class SQLText {
	
	public static final String BUILD_STREET_NETWORK = "DROP TABLE IF EXISTS  time_expanded.<city>_street_nodes CASCADE;\n"
			+ "DROP TABLE IF EXISTS  time_expanded.<city>_street_edges CASCADE;\n\n"
			+ "CREATE TABLE time_expanded.<city>_street_nodes(\n"
			+ "\tnode_in_degree integer,\n"
			+ "\tnode_out_degree integer,\n"
			+ "\tnode_id bigserial,\n"
			+ "\tnode_geometry geometry not null,\n"
			+ "\tprimary key(node_id)\n"
			+ ");\n\n"
			+ "CREATE TABLE time_expanded.<city>_street_edges(\n"
			+ "\tedge_geometry geometry,\n"
			+ "\tedge_source bigint not null,\n"
			+ "\tedge_id bigserial not null,\n"
			+ "\tedge_destination bigint not null,\n"
			+ "\tprimary key(edge_id),\n"
			+ "\tforeign key(edge_source) references time_expanded.<city>_street_nodes(node_id),\n"
			+ "\tforeign key(edge_destination) references time_expanded.<city>_street_nodes(node_id)\n"
			+ ");";	
	
	public static final String BUILD_BUS_NETWORK = "DROP TABLE IF EXISTS time_expanded.<city>_bus_calendar CASCADE;\n"
			+ "DROP TABLE IF EXISTS time_expanded.<city>_bus_nodes CASCADE;\n"
			+ "DROP TABLE IF EXISTS time_expanded.<city>_bus_routes CASCADE;\n"
			+ "DROP TABLE IF EXISTS time_expanded.<city>_bus_edges CASCADE;\n"
			+ "DROP TABLE IF EXISTS time_expanded.<city>_link CASCADE;\n"
			+ "DROP TABLE IF EXISTS time_expanded.<city>_trip_schedule CASCADE;\n\n"
			+ "CREATE TABLE time_expanded.<city>_bus_calendar (\n"
			+ "\tservice_id integer NOT NULL,\n"
			+ "\tservice_start_date text NOT NULL,\n"
			+ "\tservice_end_date text NOT NULL,\n"
			+ "\tservice_vector varchar(366) NOT NULL,\n"
			+ "\tPRIMARY KEY (service_id, service_start_date, service_end_date)\n"
			+ ");\n\n"
			+ "CREATE TABLE time_expanded.<city>_bus_routes (\n"
			+ "\troute_id  integer NOT NULL,\n"
			+ "\troute_descr_long varchar(10) NOT NULL,\n"
			+ "\troute_descr_short varchar(10) NOT NULL,\n"
			+ "\tPRIMARY KEY (route_id)\n"
			+ ");\n\n"
			+ "CREATE TABLE time_expanded.<city>_bus_nodes (\n"
			+ "\tnode_id serial NOT NULL,"
			+ "\tnode_mode integer DEFAULT 0 NOT NULL,\n"
			+ "\tnode_geometry geometry NOT NULL,\n"
			+ "\tnode_in_degree integer,\n"
			+ "\tnode_out_degree integer,\n"
			+ "\tnode_route_id integer,\n"
			+ "\tPRIMARY KEY (node_id),\n"
			+ "\tFOREIGN KEY (node_route_id) REFERENCES time_expanded.<city>_bus_routes(route_id) ON DELETE CASCADE ON UPDATE CASCADE\n"
			+ ");\n\n"
			+ "CREATE TABLE time_expanded.<city>_link (\n"
			+ "\tlink_id integer  NOT NULL,\n"
			+ "\tlink_source integer  NOT NULL,\n"
			+ "\tlink_source_mode integer  NOT NULL,\n"
			+ "\tlink_destination integer  NOT NULL,\n"
			+ "\tlink_dest_mode integer  NOT NULL,\n"
			+ "\tPRIMARY KEY ( link_id )\n"
			+ ");\n\n"
			+ "CREATE TABLE time_expanded.<city>_bus_edges ("
			+ "\tedge_id serial NOT NULL,\n"
			+ "\tedge_source integer NOT NULL,\n"
			+ "\tedge_destination integer NOT NULL,\n"
			+ "\tedge_route_id integer NOT NULL,\n"
			+ "\tPRIMARY KEY (edge_id),\n"
			+ "\tFOREIGN KEY (edge_source) REFERENCES time_expanded.<city>_bus_nodes(node_id) ON DELETE CASCADE ON UPDATE CASCADE,\n"
			+ "\tFOREIGN KEY (edge_destination) REFERENCES time_expanded.<city>_bus_nodes(node_id) ON DELETE CASCADE ON UPDATE CASCADE\n"
			+ ");\n\n"
			+ "CREATE TABLE time_expanded.<city>_trip_schedule (\n"
			+ "\ttrip_id integer NOT NULL,\n"
			+ "\ttrip_edge integer NOT NULL,\n"
			+ "\ttrip_route_id integer NOT NULL,\n"
			+ "\ttrip_time_d text NOT NULL,\n"
			+ "\ttrip_time_a text NOT NULL,\n"
			+ "\ttrip_service integer NOT NULL,\n"
			+ "\ttrip_service_s_date text NOT NULL,\n"
			+ "\ttrip_service_e_date text NOT NULL,\n"
			+ "\ttrip_seq_nr integer NOT NULL DEFAULT -1,\n"
			+ "\tPRIMARY KEY (trip_id, trip_edge, trip_route_id, trip_time_d, trip_time_a, trip_service, trip_service_s_date, trip_service_e_date, trip_seq_nr),\n"
			+ "\tFOREIGN KEY (trip_edge) REFERENCES time_expanded.<city>_bus_edges(edge_id) ON DELETE CASCADE ON UPDATE CASCADE,\n"
			+ "\tFOREIGN KEY (trip_service, trip_service_s_date, trip_service_e_date) REFERENCES time_expanded.<city>_bus_calendar(service_id, service_start_date, service_end_date) ON DELETE NO ACTION ON UPDATE NO ACTION,\n"
			+ "\tFOREIGN KEY (trip_route_id) REFERENCES time_expanded.<city>_bus_routes(route_id) ON DELETE CASCADE ON UPDATE CASCADE\n"
			+ ");";
	
	/**
	 * Text for <city>_bus_trips_import view.
	 */
	public static final String BUS_TRIPS_IMPORT = "﻿INSERT INTO time_expanded.<city>_trip_schedule(trip_id, trip_edge, trip_route_id, trip_time_d, trip_time_a, trip_service, trip_service_s_date, trip_service_e_date, trip_seq_nr)\n" +
//	public static final String BUS_TRIPS_IMPORT = "﻿INSERT INTO time_expanded.<city>_trip_schedule(trip_id, trip_edge, trip_route_id, trip_time_d, trip_time_a, trip_seq_nr)\n" +
            "\tSELECT DISTINCT bn1.t_id, bec.edge_id, bec.edge_route_id, bn1.departure_time, bn1.arrival_time, bn1.service_id, bc.service_start_date, bc.service_end_date, bn1.edge_sequence\n" +
//            "\tSELECT DISTINCT bn1.t_id as trip_id, bec.edge_id, bec.edge_route_id, bn1.departure_time, bn1.arrival_time, bn1.edge_sequence\n" +
            "\tFROM vdv_gtfs_tmp.trip_edges bn1, time_expanded.<city>_bus_edges_coord bec, time_expanded.<city>_bus_calendar bc\n" + 
            "\tWHERE bn1.route_id = bec.edge_route_id AND bn1.source_geom = bec.source_geom AND bn1.destination_geom = bec.dest_geom AND bn1.service_id = bc.service_id;\n";

}
