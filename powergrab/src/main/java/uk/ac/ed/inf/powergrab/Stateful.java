package uk.ac.ed.inf.powergrab;

import java.io.PrintWriter;

public class Stateful extends Drone{

	public Stateful(String mapString, double latitude, double longitude, int seedNum, Position position, PrintWriter writer) {
		super(mapString, latitude, longitude, seedNum, position, writer);
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
