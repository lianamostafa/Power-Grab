package uk.ac.ed.inf.powergrab;

public class Position {
	
	public double latitude;
	public double longitude;
	
	public Position(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public Position nextPosition(Direction direction) {
		
		double r = 0.0003;
		double dv = direction.getValue();
		return null;
		
//		
//		
//		double theta = Math.sqrt()
		
		
	}
	
	public boolean inPlayArea() {
		return false;
	}
}
