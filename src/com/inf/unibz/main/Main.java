package com.inf.unibz.main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import com.inf.unibz.algorithms.dijkstra.DijkstraAlgorithm;
import com.inf.unibz.database.DBConnector;
import com.inf.unibz.entity.Edge;
import com.inf.unibz.entity.Graph;
import com.inf.unibz.entity.Vertex;
import com.inf.unibz.kml.KmlReader;

public class Main {
	
	public static void main(String[] args){
		System.out.print("Choose an action: ");
		Scanner s = new Scanner(System.in);
		int action = Integer.parseInt(s.nextLine());
		s.close();
		System.out.println("You choose: " + action);
		switch(action){
		case 1:
			break;
		case 2:
			KmlReader kml = new KmlReader();
			System.out.println(kml.createStops());
			kml.updateValues(getDBConnection());
			break;
		case 3:
			DBConnector db = new DBConnector("routing");
			System.out.println("Fetching verteces...");
			List<Vertex> v = db.getPedestrianNodes();
			System.out.println("Fetching edges...");
			List<Edge> e = db.getPedestrianEdges();
			Graph g = new Graph(v, e);
			DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(g);
			System.out.println("Executing Dijkstra's shortest path...");
			long start = System.currentTimeMillis();
			dijkstra.execute(v.get(getRandomNodes(v.size())), v.get(getRandomNodes(v.size())));
			long end = System.currentTimeMillis();
			long timeSpent = end-start;
			System.out.println("Time spent: " + timeSpent + "ms");
			DBConnector.closeConnection();
			System.gc();
			break;
		}
	}
	
	public static Connection getDBConnection(){
		Connection connection = null;
		if(connection == null)
			try {
				connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/sasabus","sasabus", "sasabus");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		return connection;
	}
	
	public static int getRandomNodes(int max){
		Random r = new Random();
		return r.nextInt(max);
	}

}
