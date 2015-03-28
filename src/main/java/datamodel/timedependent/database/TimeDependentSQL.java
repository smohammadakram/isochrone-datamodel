package datamodel.timedependent.database;

public class TimeDependentSQL {

	public static final String CREATE_TABLES = "DROP TABLE IF EXISTS time_dependent.<city>_nodes CASCADE;\n" + "DROP TABLE IF EXISTS time_dependent.<city>_edges CASCADE;\n"
		+ "DROP TABLE IF EXISTS time_dependent.<city>_routes CASCADE;\n" + "DROP TABLE IF EXISTS time_dependent.<city>_trips CASCADE;\n"
		+ "DROP TABLE IF EXISTS time_dependent.<city>_calendar CASCADE;\n\n" + "CREATE TABLE IF NOT EXISTS time_dependent.<city>_nodes (\n" + "\tnode_id bigserial PRIMARY KEY,\n"
		+ "\tnode_mode smallint,\n" + "\tnode_in_degree smallint, \n" + "\tnode_out_degree smallint,\n" + "\tnode_geometry geometry\n" + ");\n\n"
		+ "CREATE TABLE IF NOT EXISTS time_dependent.<city>_edges(\n" + "\tedge_id serial PRIMARY KEY,\n" + "\tedge_source bigint,\n" + "\tedge_destination bigint, \n"
		+ "\tedge_length numeric(16,12) default 0,\n" + "\tedge_mode smallint,\n" + "\tedge_source_mode smallint,\n" + "\tedge_destination_mode smallint,\n" + "\tedge_route_id integer,\n"
		+ "\tedge_geometry geometry\n" + ");\n\n" + "CREATE TABLE IF NOT EXISTS time_dependent.<city>_routes(\n" + "\troute_id integer PRIMARY KEY,\n" + "\troute_descr_long varchar(10),\n"
		+ "\troute_descr_short varchar(10)\n" + ");\n\n" + "CREATE TABLE IF NOT EXISTS time_dependent.<city>_trips(\n" + "\ttrip_id integer,\n" + "\ttrip_route_id integer,\n"
		+ "\ttrip_source integer, \n" + "\ttrip_time_d text,\n" + "\ttrip_destination integer,\n" + "\ttrip_time_a text,\n" + "\ttrip_service_id integer,\n" + "\ttrip_edge_id integer\n" + ");\n\n"
		+ "CREATE TABLE IF NOT EXISTS time_dependent.<city>_calendar(\n" + "\tservice_id integer,\n" + "\tservice_s_date text,\n" + "\tservice_e_date text,\n" + "\tservice_vector varchar(366)\n"
		+ ");";

	public static final String INSERT_NODES = "DELETE FROM time_dependent.<city>_nodes CASCADE;\n\n" + "INSERT INTO time_dependent.<city>_nodes(node_id, node_mode, node_geometry)\n"
		+ "\tSELECT sn.node_id, -1, sn.node_geometry\n" + "\tFROM time_expanded.<city>_street_nodes sn\n" + "\tWHERE sn.node_id NOT IN\n"
		+ "\t\t(SELECT DISTINCT link_source FROM time_expanded.<city>_links) AND sn.node_id NOT IN\n" + "\t\t(SELECT DISTINCT link_destination FROM time_expanded.<city>_links);\n\n"
		+ "UPDATE time_dependent.<city>_nodes SET node_mode = 1 WHERE node_mode = -1;\n\n" + "INSERT INTO time_dependent.<city>_nodes(node_id,node_mode,node_geometry)\n"
		+ "\tSELECT DISTINCT sn.node_id, -1, sn.node_geometry\n" + "\tFROM time_expanded.<city>_street_nodes sn\n" + "\tWHERE sn.node_id IN\n"
		+ "\t\t(SELECT DISTINCT link_source FROM time_expanded.<city>_links) AND sn.node_id IN\n" + "\t\t(SELECT DISTINCT link_destination FROM time_expanded.<city>_links);\n\n"
		+ "UPDATE time_dependent.<city>_nodes SET node_mode = 0 WHERE node_mode = -1;";

	public static final String INSERT_EDGES = "INSERT INTO time_dependent.<city>_edges (edge_source, edge_destination, edge_mode, edge_source_mode, edge_destination_mode, edge_geometry)\n"
		+ "\tSELECT tee.edge_source, tee.edge_destination, 1, -1, -1, tee.edge_geometry"
		+ "\tFROM time_expanded.<city>_street_edges tee;\n\n"
//			+ "JOIN time_dependent.<city>_nodes tdn1 ON tee.edge_source = tdn1.node_id)\n"
//			+ "\t\tJOIN time_dependent.<city>_nodes tdn2 ON tee.edge_destination = tdn1.node_id AND tdn1.node_id != tdn2.node_id;\n\n"
//			+ "\tSELECT DISTINCT nose.old_source, nose.old_destination, 1, nose.old_source_mode, nose.old_dest_mode, nose.geom\n"
//			+ "\tFROM time_dependent.<city>_new_old_street_edges nose;\n\n"
		+ "\tUPDATE time_dependent.<city>_edges AS tee SET edge_source_mode = tdn.node_mode\n" + "\t\tFROM time_dependent.<city>_nodes tdn\n" + "\t\tWHERE tee.edge_source = tdn.node_id;\n\n"
		+ "\tUPDATE time_dependent.<city>_edges AS tee SET edge_destination_mode = tdn.node_mode\n" + "\t\tFROM time_dependent.<city>_nodes tdn\n"
		+ "\t\tWHERE tee.edge_destination = tdn.node_id;\n\n"
		+ "INSERT INTO time_dependent.<city>_edges (edge_source, edge_destination, edge_mode, edge_source_mode, edge_destination_mode, edge_route_id)\n"
		+ "\tSELECT DISTINCT nobe.old_bus_source, nobe.old_bus_dest, 0, 0, 0, nobe.route\n" + "\tFROM time_dependent.<city>_new_old_bus_edges nobe;";

	public static final String INSERT_ROUTES = "INSERT INTO time_dependent.<city>_routes\n" + "\tSELECT route_id, route_descr_long, route_descr_short\n" + "\tFROM time_expanded.<city>_bus_routes;";

	public static final String INSERT_CALENDAR = "INSERT INTO time_dependent.<city>_calendar\n" + "\tSELECT service_id, service_start_date, service_end_date, service_vector\n"
		+ "\tFROM time_expanded.<city>_bus_calendar;";

	public static final String INSERT_TRIPS = "INSERT INTO time_dependent.<city>_trips\n"
		+ "\tSELECT ts.trip_id, ts.trip_route_id, nobem.source, ts.trip_time_d, nobem.destination, ts.trip_time_a, ts.trip_service, nobem.old_bus_edge_id\n"
		+ "\tFROM time_dependent.<city>_new_old_bus_edges_map nobem JOIN time_expanded.<city>_trip_schedule ts ON nobem.new_bus_edge_id = ts.trip_edge;";

	public static final String NODES_EDGES_VIEWS =
//			"CREATE OR REPLACE VIEW time_dependent.<city>_new_old_nodes AS\n" //blu
//			+ "\tSELECT tdn.node_id AS old_node, ten.node_id AS new_node, tdn.node_geometry AS geometry, tdn.node_mode AS old_mode\n"
//			+ "\tFROM time_dependent.<city>_nodes tdn JOIN time_expanded.<city>_street_nodes ten ON tdn.node_geometry = ten.node_geometry;\n\n"
	"CREATE OR REPLACE VIEW time_dependent.<city>_new_old_bus_nodes AS\n"
		+ "\tSELECT tdn.node_id AS old_bus_node, tel.link_destination as new_bus_node, tdn.node_geometry AS geometry, tdn.node_mode AS old_mode\n" //fucsia
		+ "\tFROM (time_dependent.<city>_nodes tdn JOIN time_expanded.<city>_street_nodes ten ON tdn.node_geometry = ten.node_geometry)\n"
		+ "\t\tJOIN time_expanded.<city>_links tel ON ten.node_id = tel.link_source;\n\n"
//			+ "CREATE OR REPLACE VIEW time_dependent.<city>_new_old_street_edges AS\n"
//			+ "\tSELECT tdn1.new_node AS new_source, tdn2.new_node AS new_destination, tdn1.old_node AS old_source, tdn1.old_mode AS old_source_mode, tdn2.old_node AS old_destination, tdn2.old_mode AS old_dest_mode, tee.edge_source AS new_edge_source, tee.edge_destination AS new_edge_dest, tee.edge_geometry AS geom\n"
//			+ "\tFROM time_dependent.<city>_new_old_nodes tdn1, time_dependent.<city>_new_old_nodes tdn2, time_expanded.<city>_street_edges tee\n"
//			+ "\tWHERE (tdn1.new_node = tee.edge_source AND tdn2.new_node = tee.edge_destination AND tdn1.new_node != tdn2.new_node)\n"
//			+ "\t\tOR (tdn2.new_node = tee.edge_source AND tdn1.new_node = tee.edge_destination AND tdn1.new_node != tdn2.new_node);\n\n"
		+ "CREATE OR REPLACE VIEW time_dependent.<city>_new_old_bus_edges AS\n"
		+ "\tSELECT tdn1.new_bus_node AS new_source, tdn2.new_bus_node AS new_bus_destination, tdn1.old_bus_node AS old_bus_source, tdn2.old_bus_node AS old_bus_dest, tee.edge_id AS new_bus_edge, tee.edge_source AS new_bus_edge_source, tee.edge_destination AS new_bus_edge_dest, tee.edge_route_id AS route\n"
		+ "\tFROM time_dependent.<city>_new_old_bus_nodes tdn1, time_dependent.<city>_new_old_bus_nodes tdn2, time_expanded.<city>_bus_edges tee\n"
		+ "\tWHERE (tdn1.new_bus_node = tee.edge_source AND tdn2.new_bus_node = tee.edge_destination AND tdn1.new_bus_node != tdn2.new_bus_node) OR\n"
		+ "(tdn2.new_bus_node = tee.edge_source AND tdn1.new_bus_node = tee.edge_destination AND tdn1.new_bus_node != tdn2.new_bus_node);\n\n"
		+ "CREATE OR REPLACE VIEW time_dependent.<city>_new_old_bus_edges_map AS\n"
		+ "\tSELECT tde.edge_id AS old_bus_edge_id, nobe.new_bus_edge AS new_bus_edge_id, tde.edge_source AS source, tde.edge_destination AS destination\n"
		+ "\tFROM time_dependent.<city>_new_old_bus_edges nobe JOIN time_dependent.<city>_edges tde ON nobe.old_bus_source = tde.edge_source AND nobe.old_bus_dest = tde.edge_destination";

	public static final String INSERT = INSERT_NODES + "\n\n" + INSERT_EDGES + "\n\n" + INSERT_ROUTES + "\n\n" + INSERT_CALENDAR + "\n\n" + INSERT_TRIPS;

}
