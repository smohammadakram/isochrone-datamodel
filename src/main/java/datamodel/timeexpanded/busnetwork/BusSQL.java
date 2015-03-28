package datamodel.timeexpanded.busnetwork;

public final class BusSQL {

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
		+ "\tFOREIGN KEY (trip_route_id) REFERENCES time_expanded.<city>_bus_routes(route_id) ON DELETE CASCADE ON UPDATE CASCADE\n" + ");";

	/**
	 * Text for &lt;city&gt;_bus_trips_import view.
	 */
	public static final String BUS_TRIPS_IMPORT = "﻿INSERT INTO time_expanded.<city>_trip_schedule(trip_id, trip_edge, trip_route_id, trip_time_d, trip_time_a, trip_service, trip_service_s_date, trip_service_e_date, trip_seq_nr)\n"
		+
//	public static final String BUS_TRIPS_IMPORT = "﻿INSERT INTO time_expanded.<city>_trip_schedule(trip_id, trip_edge, trip_route_id, trip_time_d, trip_time_a, trip_seq_nr)\n" +
		"\tSELECT DISTINCT bn1.t_id, bec.edge_id, bec.edge_route_id, bn1.departure_time, bn1.arrival_time, bn1.service_id, bc.service_start_date, bc.service_end_date, bn1.edge_sequence\n"
		+
//            "\tSELECT DISTINCT bn1.t_id as trip_id, bec.edge_id, bec.edge_route_id, bn1.departure_time, bn1.arrival_time, bn1.edge_sequence\n" +
		"\tFROM (vdv_gtfs_tmp.trip_edges bn1 JOIN time_expanded.<city>_bus_edges_coord bec ON bn1.route_id = bec.edge_route_id AND bn1.source_geom = bec.source_geom "
		+ "\t\tAND bn1.destination_geom = bec.dest_geom) JOIN time_expanded.<city>_bus_calendar bc ON bn1.service_id = bc.service_id;\n";
//          + "\tWHERE bn1.route_id = bec.edge_route_id AND bn1.source_geom = bec.source_geom AND bn1.destination_geom = bec.dest_geom AND bn1.service_id = bc.service_id;\n";

	/**
	 * Text for &lt;city&gt;_create_bus_nodes_edges script.
	 */
	public static final String BUS_NODES_EDGES = "﻿DELETE FROM time_expanded.<city>_bus_routes;\n" + "DELETE FROM time_expanded.<city>_bus_nodes;\n"
		+ "DELETE FROM time_expanded.<city>_bus_edges;\n\n" + "INSERT INTO time_expanded.<city>_bus_routes(route_id, route_descr_long, route_descr_short)\n"
		+ "\tSELECT r.route_id, r.long_name, r.short_name\n" + "\tFROM vdv_gtfs_tmp.routes  r;\n\n" + "INSERT INTO time_expanded.<city>_bus_nodes(node_route_id, node_geometry)\n"
		+ "\tSELECT DISTINCT rs.route_id, ST_AsEWKT(rs.stop_geom)\n" + "\tFROM vdv_gtfs_tmp.route_and_stop rs;\n\n" + "CREATE OR REPLACE VIEW time_expanded.<city>_node_couple AS\n"
		+ "\tSELECT n1.node_id AS source, n2.node_id AS target, n1.node_route_id AS source_route,\n"
		+ "\tn2.node_route_id AS target_route, n1.node_geometry AS source_geom, n2.node_geometry AS target_geometry\n" + "\tFROM time_expanded.<city>_bus_nodes n1\n"
		+ "\t\tJOIN time_expanded.<city>_bus_nodes n2 ON n1.node_route_id = n2.node_route_id;\n\n" + "INSERT INTO time_expanded.<city>_bus_edges(edge_source, edge_destination, edge_route_id)\n"
		+ "\tSELECT nc.source, nc.target, nc.source_route\n" + "\tFROM  time_expanded.<city>_node_couple nc, vdv_gtfs_tmp.bus_edges be\n"
		+ "\tWHERE nc.source_geom = be.source_geom AND nc.target_geometry = be.target_geom\n" + "\t\tAND nc.source_route = be.route_id AND nc.target_route = be.r_id;\n\n"
		+ "CREATE OR REPLACE VIEW time_expanded.<city>_bus_edges_coord AS\n"
		+ "\tSELECT e.edge_id, e.edge_source, n1.node_geometry AS source_geom, e.edge_destination, n2.node_geometry AS dest_geom, e.edge_route_id\n"
		+ "\tFROM (time_expanded.<city>_bus_edges e JOIN time_expanded.<city>_bus_nodes n1 ON e.edge_source = n1.node_id)\n"
		+ "\t\tJOIN time_expanded.<city>_bus_nodes n2 ON e.edge_destination = n2.node_id;\n\n" + "CREATE OR REPLACE VIEW time_expanded.<city>_bus_trips_tmp AS\n"
		+ "\tSELECT DISTINCT bn1.t_id AS trip_id, bec.edge_id, bec.edge_route_id, bn1.departure_time, bn1.arrival_time, bn1.service_id, bn1.edge_sequence\n"
		+ "\tFROM vdv_gtfs_tmp.trip_edges bn1, time_expanded.<city>_bus_edges_coord bec\n"
		+ "\tWHERE bn1.route_id = bec.edge_route_id AND bn1.source_geom = bec.source_geom AND bn1.destination_geom = bec.dest_geom;\n";

	/**
	 * Text for &lt;city&gt;_node_couple view.
	 */
	public static final String NODE_COUPLE = "﻿CREATE OR REPLACE VIEW time_expanded.<city>_node_couple AS\n"
		+ "\tSELECT n1.node_id AS source, n2.node_id AS target, n1.node_route_id AS source_route,\n"
		+ "\tn2.node_route_id AS target_route, n1.node_geometry AS source_geom, n2.node_geometry AS target_geometry\n" + "\tFROM time_expanded.<city>_bus_nodes n1\n"
		+ "\t\tJOIN time_expanded.<city>_bus_nodes n2 ON n1.node_route_id = n2.node_route_id;\n";

	/**
	 * Text for &lt;city&gt;_bus_edges_coord view.
	 */
	public static final String BUS_EDGES_COORD = "CREATE OR REPLACE VIEW time_expanded.bus_edges_coord AS\n"
		+ "\tSELECT e.edge_id, e.edge_source, n1.node_geometry AS source_geom, e.edge_destination, n2.node_geometry AS dest_geom, e.edge_route_id, e.edge_mode\n"
		+ "\tFROM (time_expanded.bus_edges e JOIN time_expanded.bus_nodes n1 ON e.edge_source = n1.node_id)\n" + "\t\tJOIN time_expanded.bus_nodes n2 ON e.edge_destination = n2.node_id;\n";

}
