package time_expanded_spatial_data.street_network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLParser {
	
	Document doc;
	File file;
	String absolutePath;
	List<Node> allWayNodes;
	
	public XMLParser(String file){
//		setUpReader(file);
		this.file = new File(file);
		absolutePath = file;
	}
	
	public void setUpReader(String file){
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder ;
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(file);
			doc.getDocumentElement().normalize();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void printRoot(){
		System.out.println("Root element: " + doc.getDocumentElement().getNodeName());
	}
	
	public NodeList getElementByTagName(String tag){
		NodeList nList = doc.getElementsByTagName(tag);
		return nList;
	}
	
	public void printNodes(NodeList nl){
		for(int i = 0; i < 10; i++)
			for (int temp = 0; temp < nl.getLength(); temp++) {
				 
				Node nNode = nl.item(temp);
		 
				System.out.println("\nCurrent Element :" + nNode.getNodeName());
		 
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
		 
					Element eElement = (Element) nNode;
		 
					System.out.println("ID: " + eElement.getAttribute("id"));
					System.out.println("Version: " + eElement.getElementsByTagName("version").item(0).getTextContent());
					System.out.println("Timestamp: " + eElement.getElementsByTagName("timestamp").item(0).getTextContent());
					System.out.println("UID: " + eElement.getElementsByTagName("uid").item(0).getTextContent());	
					allWayNodes.add(eElement);
				}
			}
	}
	
	public void separateSourceFile(){
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			long nrOfLines = br.lines().count();
			br.close();
			br = new BufferedReader(new FileReader(file));
			System.out.println("# of lines: " + nrOfLines);
			int linesRead = 0;
			String line = "";
			for(int i = 0; i < 10; i++){
				File f = new File(file.getAbsolutePath().substring(0, absolutePath.length()-4) + "_" + i + ".osm");
				System.out.println(f.getAbsolutePath());
				BufferedWriter bw = new BufferedWriter(new FileWriter(f));
				if(i == 0)
					line = br.readLine();
				linesRead = 0;
				while(linesRead < ((int) nrOfLines/10) && line != null){
					linesRead++;
					bw.write(line + "\n");
					line = br.readLine();
				}
				bw.flush();
				bw.close();
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void readWayTag(){
		for(int i = 0; i < 10; i++){
			File f = new File(file.getAbsolutePath().substring(0, absolutePath.length()-4) + "_" + i + ".osm");
			System.out.println("Path: " + f.getAbsolutePath());
			setUpReader(f.getAbsolutePath());
			printNodes(getElementByTagName("way"));
		}
	}
	
}
