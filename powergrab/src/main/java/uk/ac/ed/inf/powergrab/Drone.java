package uk.ac.ed.inf.powergrab;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Drone {
	
	// Current number of moves implemented (MAX 250)
	public int moveCount;
	public double latitude;
	public double longitude;
	
	public String mapString;
	public String fileName;
	public PrintWriter txtWriter;
	
	public Position position;
	public Battery battery = new Battery();
	public Coins coins = new Coins();
	public Map map;
	public Direction[] directions;
	
	
	public List<Position> flightPath = new ArrayList<>();
	
	public Drone(String mapString, double latitude, double longitude, Position position, String fileName) {
		this.mapString = mapString;
		this.latitude = latitude;
		this.longitude = longitude;
		this.position = position;
		this.fileName = fileName;
		
		map = new Map(mapString);
		
		try {
			txtWriter = new PrintWriter(fileName+".txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	// Getter for the value of moveCount
	public int getCount() {
		return moveCount;
	}
}
