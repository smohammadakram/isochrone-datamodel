package data_model.time_expanded.link_netowrk;

public final class LinkSQL {

	public static final String LINK_NETWORK = "DROP TABLE IF EXISTS time_expanded.<city>_links CASCADE;\n\n"
			+ "CREATE TABLE IF NOT EXISTS time_expanded.<city>_links (\n"
			+ "\tlink_id serial, \n"
			+ "\tlink_source bigint NOT NULL,\n"
			+ "\tlink_source_mode integer NOT NULL,\n"
			+ "\tlink_destination bigint NOT NULL,\n"
			+ "\tlink_destination_mode integer NOT NULL,\n"
			+ "\tPRIMARY KEY(link_id)\n"
			+ ");" ;

//	public static final String LINK_NODES_VIEWS = "﻿CREATE OR REPLACE VIEW time_expanded.<city>_bus_nodes_projection AS\n"
//			+ "\tSELECT n.node_id, b.edge_id, MIN(ST_Distance(n.node_geometry, b.edge_geometry, true)) AS min_dist\n"
//			+ "\tFROM time_expanded.<city>_street_edges b, time_expanded.<city>_bus_nodes n\n"
//			+ "\tGROUP BY n.node_id, b.edge_id;\n\n"
//			+ "CREATE OR REPLACE VIEW time_expanded.<city>_bus_min_ped_dist AS\n"
//			+ "\tSELECT n.node_id, MIN(ST_Distance(n.node_geometry, p.edge_geometry))\n"
//			+ "\tFROM time_expanded.<city>_street_edges p, time_expanded.<city>_bus_nodes n\n"
//			+ "\tGROUP BY n.node_id;\n\n"
//			+ "CREATE OR REPLACE VIEW time_expanded.<city>_bus_to_ped_proj AS\n"
//			+ "\tSELECT pe.node_id, bnp.edge_id, pe.min\n"
//			+ "\tFROM time_expanded.<city>_bus_min_ped_dist pe, time_expanded.<city>_bus_nodes_projection bnp\n"
//			+ "\tWHERE pe.node_id = bnp.node_id AND pe.min = bnp.min_dist;\n\n"
//			+ "\tCREATE OR REPLACE VIEW time_expanded.<city>_bus_to_ped_coords_locate AS\n"
//			+ "\tSELECT DISTINCT bp.node_id,\n"
//			+ "\t\tST_Line_Locate_Point(be.edge_geometry, bn.node_geometry) AS the_geom_locate,\n"
//			+ "\t\tbn.node_geometry AS bus_node_geometry, be.edge_source, be.edge_destination,\n"
//			+ "\t\tbe.edge_geometry,\n"
//			+ "\t\tbp.edge_id\n"
//			+ "\tFROM time_expanded.<city>_bus_nodes bn, time_expanded.<city>_bus_to_ped_proj bp,\n"
//			+ "\t\ttime_expanded.<city>_street_edges be\n"
//			+ "\tWHERE bp.edge_id = be.edge_id AND bp.node_id = bn.node_id;\n\n"
//			+ "CREATE OR REPLACE VIEW time_expanded.<city>_bus_to_ped_coords_interpolate AS\n"
//			+ "\tSELECT DISTINCT l.node_id, ST_Line_Interpolate_Point(l.edge_geometry, l.the_geom_locate),\n"
//			+ "\t\tl.bus_node_geometry, l.edge_source,l.edge_destination, l.edge_geometry, l.edge_id\n"
//			+ "\tFROM time_expanded.<city>_bus_to_ped_coords_locate l;";

	public static final String PED_NODES_DEGREE_UPDATE = "﻿UPDATE time_expanded.<city>_pedestrian_nodes\n"
			+ "SET node_in_degree = '1', node_out_degree = '1'\n"
			+ "WHERE node_in_degree IS NULL AND node_out_degree IS NULL;";

	public static final String LINK_IMPORT = "﻿DELETE FROM time_expanded.<city>_link;\n"
			+ "INSERT INTO time_expanded.<city>_link (link_source, link_source_mode, link_destination, link_destination_mode)\n"
			+ "\tSELECT DISTINCT bn.node_id, bn.node_mode, pn.node_id, pn.node_mode\n"
			+ "\tFROM time_expanded.<city>_bus_nodes bn,\n"
			+ "\t\ttime_expanded.<city>_pedestrian_nodes pn,\n"
			+ "\t\ttime_expanded.<city>_bus_to_ped_coords_interpolate i\n"
			+ "\tWHERE bn.node_geometry = i.bus_node_geometry AND st_line_interpolate_point = pn.node_geometry;\n\n"
			+ "INSERT INTO time_expanded.<city>_link (link_source, link_source_mode, link_destination, link_destination_mode)\n"
			+ "\tSELECT DISTINCT pn.node_id, pn.node_mode, bn.node_id, bn.node_mode\n"
			+ "\tFROM time_expanded.<city>_bus_nodes bn,\n"
			+ "\t\ttime_expanded.<city>_street_nodes pn,\n"
			+ "\t\ttime_expanded.<city>_bus_to_ped_coords_interpolate i\n"
			+ "\tWHERE bn.node_geometry = i.bus_node_geometry AND st_line_interpolate_point = pn.node_geometry;";

	public static final String DATA_TO_EDGES_SNAP = "﻿CREATE OR REPLACE VIEW time_expanded.<city>_data_for_edges_snap AS\n"
			+ "\tSELECT DISTINCT bc.edge_id, bc.edge_source, bc.edge_destination, pn.node_id AS bus_ped_node_id,\n"
			+ "\t\tpn.node_geometry, bc.edge_geometry\n"
			+ "\tFROM time_expanded.<city>_street_nodes pn, time_expanded.<city>_bus_to_ped_coords_interpolate bc\n"
			+ "\tWHERE bc.st_line_interpolate_point = pn.node_geometry AND pn.node_in_degree = '1' AND pn.node_out_degree = '1';";

	public static final String EDGE_TO_MERGE = "﻿CREATE VIEW time_expanded.<city>_edges_to_merge AS\n"
			+ "\tSELECT b1.edge_id as source_edge_id, b1.edge_source as source, b1.edge_length as length_source, b1.edge_geometry source_geometry,\n"
			+ "\t\tb2.edge_id as dest_edge_id, b2.edge_destination as dest, b2.edge_length as dest_length, b2.edge_geometry as dest_geometry,\n"
			+ "\t\tbn.node_id, bn.node_in_degree, bn.node_out_degree\n"
			+ "\tFROM time_expanded.<city>_street_edges b1, time_expanded.<city>_street_edges b2, time_expanded.<city>_bus_ped_network bn\n"
			+ "\tWHERE b1.edge_destination = b2.edge_source AND b1.edge_source != b2.edge_destination AND b1.edge_destination = bn.node_id\n"
			+ "\t\tAND bn.node_in_degree = '1' AND bn.node_out_degree = '1';";

}
