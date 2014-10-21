package sasa_importer;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.postgis.PGgeometry;

public class LinkNetwork {
	
	private DBConnector db;
	
	public LinkNetwork(DBConnector db){
		this.db = db;
	}
	
	public void createLinkNode(){
		int lastIndex = db.getLastPedestrianNodeID();
		ResultSet rs = db.executeSimpleQuery("SELECT node_id, ST_AsEWKT(st_line_interpolate_point) "
				+ "FROM bz_isochrones_2014.bus_to_ped_coords_interpolate;");
		try {
			String sql = "";
			if(rs.first()){
				while(rs.next()){
					System.out.println((String) rs.getObject(2));
					System.out.println(++lastIndex);
					sql = "INSERT INTO bz_isochrones_2014.bz_pedestrian_nodes(node_id, node_geometry) "
							+ "VALUES('" + ++lastIndex + "', ST_GeomFromEWKT('" + ((String) rs.getObject(2)) + "'));";
					System.out.println(sql);
					db.executeSimpleQuery(sql);
					db.executeSimpleQuery("UPDATE bz_isochrones_2014.bz_pedestrian_nodes "
							+ "SET node_in_degree = '1', node_out_degree = '1' "
							+ "WHERE node_in_degree IS NULL AND node_out_degree IS NULL; ");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updatePedestrianNetwork(){
		int lastIndex = db.getLastPedestrianEdgeID();
		try {
			System.out.println("Retrieving data...");
		ResultSet rs = db.executeSimpleQuery("SELECT edge_id, edge_source, edge_destination, bus_ped_node_id, ST_AsEWKT(edge_geometry), ST_AsEWKT(node_geometry) "
				+ "FROM bz_isochrones_2014.data_for_edges_snap;");
		if(rs.first())
			while(rs.next()){
				String edgeGeometry = (String) rs.getObject(5);
				String nodeGeometry = (String) rs.getObject(6);
				System.out.println(rs.toString());
				System.out.println("Updating old edges...");
				db.executeSimpleQuery("UPDATE bz_isochrones_2014.bz_pedestrian_edges "
						+ "SET edge_geometry = ST_Snap(ST_GeomFromEWKT('" + edgeGeometry + "'),  ST_GeomFromEWKT('" + nodeGeometry + "'), 1), "
						+ "edge_destination = '" + rs.getInt(3) + "', "
						+ "edge_length = ST_Length(ST_Snap(ST_GeomFromEWKT('" + edgeGeometry + "'), ST_GeomFromEWKT('" + nodeGeometry + "'), 1)) "
						+ "WHERE edge_id = '" + rs.getInt(1) + "';");
				System.out.println("Inserting new edges...");
				db.executeSimpleQuery("INSERT INTO bz_isochrones_2014.bz_pedestrian_edges (edge_id, edge_source, edge_destination, edge_length, edge_geometry) "
						+ "VALUES('" + ++lastIndex + "', '" + rs.getInt(4) + "', '" + rs.getInt(3) + "', "
								+ "ST_Length(ST_Difference('" + edgeGeometry + "', ST_Snap(ST_GeomFromEWKT('" + edgeGeometry + "'), ST_GeomFromEWKT('" + nodeGeometry + "'), 1))), "
										+ "ST_Difference('" + edgeGeometry + "', ST_Snap(ST_GeomFromEWKT('" + edgeGeometry + "'), ST_GeomFromEWKT('" + nodeGeometry + "'), 1)));");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
