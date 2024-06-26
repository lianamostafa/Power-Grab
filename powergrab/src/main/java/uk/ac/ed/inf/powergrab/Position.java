package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
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
		// I used 90 - direction_angle for the angle as I had set North to 0 in my direction enum
		double direction_angle = 90 - direction.getAngle();
		double new_latitude = 0;
		double new_longitude = 0;
		
		
		/* The longitude and latitude were calculated using the basic trigonometric formulas
		 * r*cos(angle) and r*sin(angle) respectively; 
		 * before being added to the old longitude and latitude
		 *
		 * The angles were then converted to radians
		 */
		
		new_longitude = longitude + r*(Math.cos(Math.toRadians(direction_angle)));
		new_latitude = latitude + r*(Math.sin(Math.toRadians(direction_angle)));
		
		return new Position(new_latitude, new_longitude);
	}
	
	/* Function to get the Euclidean distance between the current position and a feature
	 * (providing the coordinates of said feature).
	 */
	
	public double getDist(List<Double> nextCoords) {
		
		double nextLong = nextCoords.get(0);
		double nextLat = nextCoords.get(1);
		
		double longs = nextLong-longitude;
		double lats = nextLat-latitude;
		double dist = Math.sqrt(((longs)*(longs))+((lats)*(lats)));
		
		return dist;
	}
	
	// Function to check if the given distance is less than or equal to 0.00025
	public boolean inRange(double dist) {
		if(dist <= 0.00025) {
			return true;
		} else {
			return false;
		}
	}
	
	// Getter for the coordinates of the current position
	public List<Double> getCoordinates(){
		List<Double> coordinates = new ArrayList<>();
		coordinates.add(longitude);
		coordinates.add(latitude);
		return coordinates;
	}
	
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
