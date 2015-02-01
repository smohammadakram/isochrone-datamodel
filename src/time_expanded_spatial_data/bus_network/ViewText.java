package time_expanded_spatial_data.bus_network;

public final class ViewText {

	/**
	 * Text for <city>_create_bus_nodes_edges view.
	 */
	public static final String CREATE_BUS_NODES_EDGES = 
			"﻿DELETE FROM time_expanded.<city>_bus_routes;\n" +
			"DELETE FROM time_expanded.<city>_bus_nodes;\n" +
			"DELETE FROM time_expanded.<city>_bus_edges;\n\n" +
			"INSERT INTO time_expanded.<city>_bus_routes(route_id, route_descr_long, route_descr_short)\n" +
			"\tSELECT r.route_id, r.long_name, r.short_name\n" +
			"\tFROM vdv_gtfs_tmp.routes  r;\n\n" +
			"INSERT INTO time_expanded.<city>_bus_nodes(node_route_id, node_geometry)\n" +
			"\tSELECT DISTINCT rs.route_id, ST_AsEWKT(rs.stop_geom)\n" +
			"\tFROM vdv_gtfs_tmp.route_and_stop rs;\n\n" +
			"CREATE OR REPLACE VIEW time_expanded.<city>node_couple AS\n" + 
			"\tSELECT n1.node_id AS source, n2.node_id AS target, n1.node_route_id AS source_route,\n" + 
			"\tn2.node_route_id AS target_route, n1.node_geometry AS source_geom, n2.node_geometry AS target_geometry\n" +
			"\tFROM time_expanded.<city>_bus_nodes n1\n" +
			"\t\tJOIN time_expanded.<city>_bus_nodes n2 ON n1.node_route_id = n2.node_route_id;\n\n" + 
			"INSERT INTO time_expanded.<city>_bus_edges(edge_source, edge_destination, edge_route_id)\n" +
			"\tSELECT nc.source, nc.target, nc.source_route\n" +
			"\tFROM  time_expanded.<city>_node_couple nc, vdv_gtfs_tmp.bus_edges be\n" +
			"\tWHERE nc.source_geom = be.source_geom AND nc.target_geometry = be.target_geom\n" + 
			"\t\tAND nc.source_route = be.route_id AND nc.target_route = be.r_id;\n\n" +
			"CREATE OR REPLACE VIEW time_expanded.<city>_bus_edges_coord AS" +
			"\tSELECT e.edge_id, e.edge_source, n1.node_geometry AS source_geom, e.edge_destination, n2.node_geometry AS dest_geom, e.edge_route_id, e.edge_mode\n" + 
			"\tFROM (time_expanded.<city>_bus_edges e JOIN time_expanded.<city>_bus_nodes n1 ON e.edge_source = n1.node_id)\n" +
			"\t\tJOIN time_expanded.<city>_bus_nodes n2 ON e.edge_destination = n2.node_id;\n\n" +
			"CREATE OR REPLACE VIEW time_expanded.<city>_bus_trips_tmp AS\n" + 
			"\tSELECT DISTINCT bn1.t_id AS trip_id, bec.edge_id, bec.edge_route_id, bn1.departure_time, bn1.arrival_time, bn1.service_id, bn1.edge_sequence\n" +
			"\tFROM vdv_gtfs_tmp.trip_edges bn1, time_expanded.<city>_bus_edges_coord bec\n" +
			"\tWHERE bn1.route_id = bec.edge_route_id AND bn1.source_geom = bec.source_geom AND bn1.destination_geom = bec.dest_geom;\n";
	
	/**
	 * Text for <city>_bus_trips_import view.
	 */
	public static final String BUS_TRIPS_IMPORT = "";
	
	/**
	 * Text for <city>_node_couple view.
	 */
	public static final String NODE_COUPLE = "";
	
	/**
	 * Text for <city>_bus_edges_coord view.
	 */
	public static final String BUS_EDGES_COORD = "";
}
