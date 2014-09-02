package com.inf.unibz.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.io.File;

public class SFDatabaseParser {
	
	private final static String INPUT_FILE = "C:\\Users\\Luca\\Dropbox\\Uni\\Thesis\\Datasets\\sf_export - Copy.sql";
	private final static String OUTPUT_FILE = "C:\\Users\\Luca\\Dropbox\\Uni\\Thesis\\Datasets\\sf_export_edited.sql";
	private BufferedReader br;
	private BufferedWriter bw;
	private int check;
	
	public SFDatabaseParser(){
		check = 1;
	}
	
	public BufferedReader getFileBufferedReader(){
		try {
			br = new BufferedReader(new FileReader(INPUT_FILE));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return br;
	}
	
	public BufferedWriter getFileBufferedWriter(){
		try {
			bw = new BufferedWriter(new FileWriter(OUTPUT_FILE));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bw;
	}
	
	public void writeFile(){
		BufferedReader br = getFileBufferedReader();
		BufferedWriter bw = getFileBufferedWriter();
		try {
			String line = br.readLine();
			while(line != null){
				int rest = check%10;
				checkLine(line);
				if(rest == 0)
					System.out.println("Check point " + check);
				check++;
				line = br.readLine();
			}
			br.close();
			bw.close();			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void checkLine(String line){
		String[] token = line.split(" ");
		String writer = "";
		switch(token[0]){
		case "CREATE": case "ALTER": case "SET":
			writer += line;
			try {
				bw.write(writer);
				bw.flush();
				line = br.readLine();
				while(!line.contains(";")){
					writer = "";
					int res = check%10;
					if(res == 0)
						System.out.println("Check point " + check);
					check++;
					writer += line + "\n";
					bw.write(writer);
					bw.flush();
					line = br.readLine();
				}
				writer = line + "\n\n";
				bw.write(writer);
				bw.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			break;
		
		case "COPY":
			try {
			writer += "INSERT INTO " + token[1] + " " + getInsertColumns(line) + " VALUES \n";
			bw.write(writer);
			line = br.readLine();
			while(!line.equals("\\.")){
				writer = "";
				int res = check%10;
				if(res == 0)
					System.out.println("Check point " + check);
				check++;
				writer += getDataRow(line);
				line = br.readLine();
				if(!line.equals("\\."))
					writer += ",\n";
				bw.write(writer);
				bw.flush();
			}
			writer = ";\n\n";
			bw.write(writer);
			bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		}	
			
	}
	
	public String getInsertColumns(String line){
		String[] token = line.split(" ");
		String columns = "";
		for(int i = 2; i < token.length-2; i++){
			columns += token[i];
			if(i != token.length-3)
				columns += " ";
		}
		return columns;
	}
	
	public String getDataRow(String currentLine){
		String[] token = currentLine.split("\t");
		String row = "(";
		for(int i = 0; i < token.length; i++){
			if(token[i].equals("NULL"))
				row += "'-1'";
			else
				row += "'"  + token[i] + "'";
			if(i < token.length-1)
				row += ", ";
		}
		row += ")";
		return row;
	}

}
