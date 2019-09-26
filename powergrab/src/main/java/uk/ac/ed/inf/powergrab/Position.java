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
		double da = direction.getAngle();
		double new_latitude = 0;
		double new_longitude = 0;
		
		// If the direction is between North and East
		if(da >= 0 && da <= 112.5) {
			
			new_longitude = longitude + r*(Math.cos(Math.toRadians(90-da)));
			new_latitude = latitude + r*(Math.sin(Math.toRadians(90-da)));
			
		// If the direction is between South and West
		} else if(da >= 135 && da <= 337.5) {
			
			new_longitude = longitude - r*(Math.cos(Math.toRadians(90-da)));
			new_latitude = latitude - r*(Math.sin(Math.toRadians(90-da)));
			
		}
		
		return new Position(new_latitude, new_longitude);
	}
	
	public boolean inPlayArea() {
		if((latitude >= 55.942617 && latitude <= 55.946233) && (longitude >= -3.192473 && longitude <= -3.184319)) {
			return true;
		} else {
			return false;
		}
	}
}
