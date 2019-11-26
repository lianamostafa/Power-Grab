package uk.ac.ed.inf.powergrab;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Drone {
	
	public String mapString;
	public double latitude;
	public double longitude;
	public int seedNum;
	
	public Position position;
	public Battery battery = new Battery();
	public Coins coins = new Coins();
	public Map map = new Map(mapString);
	public String fileName;
	public PrintWriter txtWriter;
	
	
	public Drone(String mapString, double latitude, double longitude, int seedNum, Position position, String fileName) {
		this.mapString = mapString;
		this.latitude = latitude;
		this.longitude = longitude;
		this.seedNum = seedNum;
		this.position = position;
		this.fileName = fileName;
		
		try {
			txtWriter = new PrintWriter(fileName+".txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	

}
