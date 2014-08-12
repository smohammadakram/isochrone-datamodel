package com.inf.unibz.main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

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

}
