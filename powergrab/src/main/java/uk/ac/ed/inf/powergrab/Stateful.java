package uk.ac.ed.inf.powergrab;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Stateful{
	
	// Current number of moves implemented (MAX 250)
	private int count;
	private RandomDirectionGenerator rdg;
	public List<Position> totalMoves = new ArrayList<Position>();
	
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

	public Stateful(String mapString, double latitude, double longitude, int seedNum, Position position, String fileName) {
		System.out.println("we get here");
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
	
	public void Move() {
//		Position nextPos = pos.nextPosition(d);
//		
//		if(nextPos.inPlayArea()) {
//			pos.latitude = nextPos.latitude;
//			pos.longitude = nextPos.longitude;
//			battery.consumeBattery(1.25);
//			moves.add(pos);
//			count++;
//		} 
	}
	

}
