package uk.ac.ed.inf.powergrab;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import com.mapbox.geojson.Feature;

public class Stateful{
	
	// Current number of moves implemented (MAX 250)
	private int moveCount;
	public double latitude;
	public double longitude;
	
	public String mapString;
	public String fileName;
	public PrintWriter txtWriter;
	
	public Position position;
	public Battery battery = new Battery();
	public Coins coins = new Coins();
	public Map map;
	
	public List<Position> flightPath = new ArrayList<>();
	public List<List<Double>> flightCoordinates = new ArrayList<>();

	public Stateful(String mapString, double latitude, double longitude, Position position, String fileName) {
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
	
	public void Move() {

		// Add base position to flightPath
		flightPath.add(new Position(position.latitude, position.longitude));
		
		// Add base coordinates to flightCoordinates
		flightCoordinates.add(new ArrayList<>(position.getCoordinates()));
		
		Direction[] directions = Direction.values();
		
		/* As stateful aims to visit every good charging station, we need to also stop
		 * when there are no more charging stations to visit.
		 */

		while(moveCount <= 250 && battery.getCharge() >= 1.25 && !map.goodFeatures.isEmpty()) {
			
			List<Direction> illegalDirections = new ArrayList<>();
			HashMap<Direction, Double> distances = new HashMap<>();
			
			// Get closest available charging station we can visit
			Feature closestFeature = getClosestFeature(map.goodFeatures);
			Direction closestDirection = null;
			boolean arrived = false;
			
			//Write latitude and longitude to the file
			txtWriter.print(position.latitude + " ");
			txtWriter.print(position.longitude + " ");
			
			// While we haven't yet arrived at our charging station
			while(!arrived) {
				
				// If we hit 250 moves, exit loop
				if(moveCount == 250) {
					break;
				}
				
				/* Check the distance between the position at each direction
				 * and the charging station we are trying to visit.
				 * Then, put the direction and the associated distance in
				 * our HashMap "distances".
				 * 
				 * Filter out any bad directions, similar to how we did it
				 * in stateful.
				 */
				
				for(Direction currentDirection : directions) {
					
					Position next = position.nextPosition(currentDirection);
					double currentDistance = next.getDist(map.getCoordinates(closestFeature));
					
					/* If the next position is not in the play area, add the direction
					 * to our illegalDirections.
					 */
					
					if(!next.inPlayArea()) {
						illegalDirections.add(currentDirection);
						continue;
					}
					
					/* If the next position's coordinates already exists in our
					 * flightCoordinates list, add the direction to our illegalDirections
					 * as we do not want the drone to repeat positions.
					 */
					
					if(flightCoordinates.contains(next.getCoordinates())) {
						illegalDirections.add(currentDirection);
						continue;
					}
					
					/* If our next position will take us in range of any bad charging stations,
					 * add the associated direction to our illegalDirections list;
					 */
					
					for(Feature bf : map.badFeatures) {
						if(next.inRange(next.getDist(map.getCoordinates(bf)))) {
							illegalDirections.add(currentDirection);
							break;
						}
					}
					
					/* If the current direction is not in illegalDirections,
					 * add the direction and associated distance to our distances HashMap
					 */
					
					if(!illegalDirections.contains(currentDirection)) {
						distances.put(currentDirection, currentDistance);
						
						/* If the next position brings us in range of the desired
						 * good charging station, set arrived to true and break
						 * out of the for loop.
						 * 
						 * We can also now remove closestFeature from map.goodFeatures
						 * as we no longer need to move towards it.
						 */
						
						if(next.inRange(currentDistance) ) {
							closestDirection = currentDirection;
							map.goodFeatures.remove(closestFeature);
							arrived = true;
							break;
						}
					}
				}
				
				// If we have arrived, break out of the while loop.
				if(arrived) {
					break;
				}
				
				/* If we haven't arrived at the feature we want yet, but can now 
				 * choose a move to make:
				 * 
				 * Find the direction which will bring us closest to the station
				 * we want to visit, by getting the minimum distance from our
				 * HashMap distances.
				 */
				Double closestDistance = Collections.min(distances.values());
				List<Direction> keys = new ArrayList<>(distances.keySet());
				
				for(int i = 0; i < distances.size(); i++) {
					Direction currentDirection = keys.get(i);
					if(distances.get(currentDirection) == closestDistance) {
						closestDirection = currentDirection;
						break;
					}
				}
				
				// Clear illegalDirections and distances for the next iteration
				illegalDirections.clear();
				distances.clear();
				
				// Take the closestDirection to endMove to complete the move
				endMove(closestDirection);
			}
			
			// What to do once we've arrived at the feature we want
			battery.chargeBattery(map.getPower(closestFeature));
			coins.addCoins(map.getCoins(closestFeature));
			int bestFeatureIndex = map.features.indexOf(closestFeature);
			map.features.get(bestFeatureIndex).removeProperty("coins");
			map.features.get(bestFeatureIndex).removeProperty("power");
			map.features.get(bestFeatureIndex).addNumberProperty("coins", 0.0);
			map.features.get(bestFeatureIndex).addNumberProperty("power", 0.0);
			
			// Take the closestDirection to endMove to complete the move
			endMove(closestDirection);
		}
		map.writeFlightPath(flightPath, fileName);
	}
	
	// Helper function for everything we need to do once we have decided on a move to make
	public void endMove(Direction closestDirection) {
	
		Position bestPosition = position.nextPosition(closestDirection);
		
		// Write direction of move to the file
		txtWriter.print(closestDirection + " ");
		
		// Set our latitude and longitude to match those found in out bestPosition
		position.latitude = bestPosition.latitude;
		position.longitude = bestPosition.longitude;
		
		// Write new latitude and longitude to the file
		txtWriter.print(position.latitude + " ");
		txtWriter.print(position.longitude + " ");
		
		battery.consumeBattery(1.25);
		
		// Write new value of coins after move to file
		txtWriter.print(coins.getCoins() + " ");
		
		// Write new value of battery after move to file
		txtWriter.print(battery.getCharge() + " ");
		// Print new line on file 
		txtWriter.println("");
		
		// Add new position to flightPath
		flightPath.add(new Position(position.latitude, position.longitude));
		
		// Add coordinates to flightCoordinates
		flightCoordinates.add(new ArrayList<>(position.getCoordinates()));
				
		// Move complete, increment moveCount
		moveCount++;
		
	}
	
	public Feature getClosestFeature(List<Feature> currentFeatures) {
		
		/* This function takes in the current list of unvisited features
		 * and returns whichever one is closest to our drone's current position.
		 */
		
		HashMap<Feature, Double> distances = new HashMap<>();
		
		for(int i = 0; i < currentFeatures.size(); i++) {
			double currentDistance = position.getDist(map.getCoordinates(currentFeatures.get(i)));
			Feature currentFeature = currentFeatures.get(i);
			distances.put(currentFeature, currentDistance);
		}
		
		Double closestDistance = Collections.min(distances.values());
		
		/* If there are any duplicate features that are the same min distance away,
		 * pick the one that will give us the highest number of coins.
		 */
		
		List<Feature> closestFeatures = new ArrayList<>();
		
		for(Feature f : currentFeatures) {
			if(distances.get(f) == closestDistance){
				closestFeatures.add(f);
			}
		}
		
		if(closestFeatures.size() > 1) {
			Feature bestFeature = closestFeatures.get(0);
			double bestCoins = map.getCoins(bestFeature);
			
			for(int i = 1; i < closestFeatures.size(); i++) {
				Feature currentFeature = closestFeatures.get(i);
				double currentCoins = map.getCoins(currentFeature);
				if(currentCoins > bestCoins) {
					bestCoins = currentCoins;
					bestFeature = currentFeature;
				}
			}
			return bestFeature;
		} else {
			// Otherwise just return the closest feature
			return closestFeatures.get(0);
		}
		
	}
	
	// Getter for the value of moveCount
	public int getCount() {
		return moveCount;
	}
}
