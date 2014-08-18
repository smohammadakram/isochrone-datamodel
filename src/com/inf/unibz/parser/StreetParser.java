package com.inf.unibz.parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class StreetParser {
	
	public static void main(String[] args){
		try {
			Connection connection = null;
			connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/sasabus","sasabus", "sasabus");
			Statement stmt = connection.createStatement();
			BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Luca\\Dropbox\\Uni\\Thesis\\SASA\\street data bz\\Wege_filtered.csv"));
			String[] fields = new String[14];
			String[] fieldsType = {"integer",
					"integer",
					"varchar(50)", 
					"varchar(50)", 
					"integer",
					"varchar(50)",
					"varchar(50)",
					"integer",
					"integer",
					"double precision",
					"integer",
					"integer",
					"integer",
					"integer",
					"geometry"};
			String[] recordData = null;
			String line = br.readLine();
			String[] lineTokens = line.split(",");
			String dropTable = "DROP TABLE IF EXISTS vdv.streets;";
			stmt.execute(dropTable);
			String createTable = "CREATE TABLE vdv.streets (";
			for(int i = 0; i < 14; i++){
				fields[i] = lineTokens[i];
				createTable += fields[i].toLowerCase() + " " + fieldsType[i].toUpperCase();
				if(i < 13)
					createTable += ", ";
			} 
			createTable += ", street_start_geometry GEOMETRY, street_end_geometry GEOMETRY, street_geometry GEOMETRY, CONSTRAINT street_primary_key PRIMARY KEY (" + fields[0] + "));";
			System.out.println(createTable);
			stmt.execute(createTable);
//			connection.commit();
			line = br.readLine();
//			System.out.println(fieldsType.length);
			while(line != null){
				recordData =  line.split(",");
				System.out.println(recordData.length);
				String recordBuilder = "INSERT INTO vdv.streets (" + getFieldsAsString(fields) + "street_start_geometry, street_end_geometry, street_geometry) "
						+ "VALUES (" + getDataAsString(recordData, fieldsType) 
						+ "ST_Force_2D(ST_Makepoint(" + recordData[10] + ", " + recordData[11] +")),\nST_Force_2D(ST_Makepoint(" + recordData[12] + ", " + recordData[13] 
						+ ")),\nST_SetSRID(ST_Makeline(ST_Force_2D(ST_Makepoint(" + recordData[10] + ", " + recordData[11] + ")), ST_Force_2D(ST_Makepoint(" + recordData[10] + ", " + recordData[11] + "))), 25832));";
				System.out.println(recordBuilder);
				System.out.println();
				stmt.execute(recordBuilder);
				line = br.readLine();
				recordData = null;
			}
			
			br.close();
		} 
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(IOException e ){
			e.printStackTrace();
		}
		
		catch (SQLException e){
			e.printStackTrace();
		}
		
	}
	
	public static String getFieldsAsString(String[] fields){
		String result = "";
		for(int i = 0; i < 14; i++){
			result += fields[i];
//			if(i < 13)
				result += ", ";
		}
		return result;
	}

	public static String getDataAsString(String[] fields, String[] fieldsTypes){
		String result = "";
		for(int i = 0; i < 14; i++){
			if(fields[i].contains("'")){
				System.out.println(fields[i]);
				int idx = fields[i].indexOf("'");
				int endIdx = fields[i].length();
				fields[i] = fields[i].substring(0, idx) + " " + fields[i].substring(idx+1, endIdx);
				System.out.println(fields[i]);
			}
			if(fieldsTypes[i].equals("integer") || fieldsTypes[i].equals("double precision"))
				result += fields[i];
			else
				result += "'" + fields[i] + "'"; 
//			if(i < 13)
				result += ",\n";
		}
		return result;
	}
}
