package com.inf.unibz.main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import com.inf.unibz.algorithms.dijkstra.DijkstraAlgorithm;
import com.inf.unibz.data_structure.adjacency.AdjacencyList;
import com.inf.unibz.data_structure.adjacency.AdjacencyMatrix;
import com.inf.unibz.database.DBConnector;
import com.inf.unibz.entity.Edge;
import com.inf.unibz.entity.Graph;
import com.inf.unibz.entity.Vertex;
import com.inf.unibz.kml.KmlReader;
import com.inf.unibz.parser.SFDatabaseParser;

public class Main {
	
	private final static String RESULTS = "C:\\Users\\Luca\\Dropbox\\Uni\\Thesis\\Results\\result.txt";
	private static Hashtable<Integer, Integer> vertecesIndexes;
	
	public static void main(String[] args){
		System.out.print("Choose an action: ");
		Scanner s = new Scanner(System.in);
		int action = Integer.parseInt(s.nextLine());
		s.close();
		System.out.println("You choose: " + action);
		DBConnector db = null;
		switch(action){
		
		case 1:
			break;
			
		case 2:
			KmlReader kml = new KmlReader();
			System.out.println(kml.createStops());
			kml.updateValues(getDBConnection());
			break;
			
		case 3:
			
			try {
				db = new DBConnector("routing");
				BufferedWriter bw = new BufferedWriter(new FileWriter(RESULTS));
				vertecesIndexes = generateVertecesIndexes(db.getAllNodesAsArray());
				
				//setting environment
				System.out.println("Fetching verteces...");
				List<Vertex> v = db.getAllNodes();
				System.out.println("Fetching edges...");
				List<Edge> e = db.getAllEdges();
				Graph g = new Graph(v, e);
				System.out.println("Building matrix...");
				
				DijkstraAlgorithm def, matx, lst = null;
				long start, end = -1;
				String defaultPath, matrixPath, listPath = null;
				
				//generating random nodes for algorithm
				
				//building adjacency matrix
				AdjacencyMatrix matrix = new AdjacencyMatrix();
				matrix.setMatrixWithoutTime();
	//			matrix.setMatrixWithTime();
				System.out.println("Matrix built!");
				
				//building adjacency list
				AdjacencyList list = new AdjacencyList();
				list.setListWithoutTime();
//				list.setListWithTime();
				System.out.println("List built!");
				
				for(int i = 0; i <= 50; i++){
					
					def = new DijkstraAlgorithm(g);
					matx = new DijkstraAlgorithm(matrix);
					lst = new DijkstraAlgorithm(list, e); 
					def.setVertecesIndexes(vertecesIndexes);
					matx.setVertecesIndexes(vertecesIndexes);
					lst.setVertecesIndexes(vertecesIndexes);
					
					//getting random nodes for path
					Vertex source = v.get(getRandomNodes(v.size()));
					Vertex destination = v.get(getRandomNodes(v.size()));
					bw.write("Source: " + source.toString() + "\n");
					bw.write("Destination: " + destination.toString() + "\n");
					
					//running Djikstra with no specific data structure
					start = System.currentTimeMillis();
					def.execute(source, destination);
					end = System.currentTimeMillis();
					defaultPath = def.getPath(destination).toString();
					long timeSpentDefault = end-start;
					
					//running Djikstra with adjacency matrix
					start = System.currentTimeMillis();
					matx.executeMatrix(source, destination);
					end = System.currentTimeMillis();
					matrixPath = matx.getPath(destination).toString();
					long timeSpentMatrixNoTime = end-start;			
					
					//runnging Djikstra with adjacency list
					System.out.println("Executing computation with list...");
					start = System.currentTimeMillis();
					lst.executeList(source, destination);
					end = System.currentTimeMillis();
					listPath = lst.getPath(destination).toString();
					long timeSpentListNoTime = end-start;			
					
					//final results
					bw.write("Time spent for default structure without timetable " + i + ": " + timeSpentDefault + "ms\n");
					bw.write("Time spent for matrix without timetable " + i + ": " + timeSpentMatrixNoTime + "ms\n");
					bw.write("Time spent for list without timetable: " + timeSpentListNoTime + "ms\n");
					
					if(defaultPath.equals(matrixPath) && defaultPath.equals(listPath))
						bw.write("Paths are equal.");
					else
						bw.write("Paths are not equal.");
					bw.write("\n\n");
					bw.flush();
					System.out.println("Query nr. " + i);
				}
				
				//closing
				bw.close();
				DBConnector.closeConnection();
				System.gc();
				System.out.println("Process terminated.");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			break;
			
		case 4:
			db = new DBConnector("routing");
			List<Edge> edges = db.getBusEdges();
			for(Edge ed : edges)
				db.getTripConnectionsByEdge(ed.getId());
		case 5:
			
			SFDatabaseParser parser = new SFDatabaseParser();
			parser.writeFile();
			System.out.println("File parsed.");
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
	
	public static Hashtable<Integer, Integer> generateVertecesIndexes(int[] verteces){
		if(vertecesIndexes == null)
			vertecesIndexes = new Hashtable<Integer, Integer>();
		for(int i = 0; i < verteces.length; i++)
			vertecesIndexes.put(verteces[i], i);
		return vertecesIndexes;
	}

}
