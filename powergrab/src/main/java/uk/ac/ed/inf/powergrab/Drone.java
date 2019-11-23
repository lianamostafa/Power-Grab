package uk.ac.ed.inf.powergrab;

public class Drone {
	
	public String mapString;
	public double latitude;
	public double longitude;
	public int seedNum;
	
	public Position position;
	public Battery battery = new Battery();
	public Map map = new Map(mapString);
	
	
	public Drone(String mapString, double latitude, double longitude, int seedNum, Position position) {
		this.mapString = mapString;
		this.latitude = latitude;
		this.longitude = longitude;
		this.seedNum = seedNum;
		this.position = position;
	}
	
	

}
