package uk.ac.ed.inf.powergrab;

import java.util.List;

public class Position {
	
	public double latitude;
	public double longitude;
	
	public Position(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public Position nextPosition(Direction direction) {
		
		double r = 0.0003;
		double direction_angle = direction.getAngle();
		double new_latitude = 0;
		double new_longitude = 0;
		
		
		/* The longitude and latitude were calculated using the basic trigonometric formulas
		 * r*cos(angle) and r*sin(angle) respectively; 
		 * before being added to the old longitude and latitude
		 */
		
		/* I used 90 - direction_angle for the angle as I had set North to 0 in my direction enum
		 * The angles were then converted to radians
		 */
		
		new_longitude = longitude + r*(Math.cos(Math.toRadians(90-direction_angle)));
		new_latitude = latitude + r*(Math.sin(Math.toRadians(90-direction_angle)));
		
		return new Position(new_latitude, new_longitude);
	}
	
//	public boolean inRange(List<Double> nextCoords) {
//		//TODO calculate euclidean distance between nextcoords and current lat/long
//		//Remember that GeoJSON holds long first and then lat
//		
//	}
	
	public boolean inPlayArea() {
		
		/* inPlayArea is a simple function to check if the drone's current position 
		 * lies within the designated space
		 * This is calculated by ensuring that the current longitude/latitude is between
		 * the maximum/minimum longitude/latitude
		 */
		
		if((latitude > 55.942617 && latitude < 55.946233) 
			&& (longitude > -3.192473 && longitude < -3.184319)) {
			return true;
		} else {
			return false;
		}
	}
}
