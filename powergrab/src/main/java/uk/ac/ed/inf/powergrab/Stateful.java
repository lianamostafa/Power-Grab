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
	private int count = 0;
	public int seedNum;
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

	public Stateful(String mapString, double latitude, double longitude, int seedNum, Position position, String fileName) {
		this.mapString = mapString;
		this.latitude = latitude;
		this.longitude = longitude;
		this.seedNum = seedNum;
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
		Direction[] directions = Direction.values();
		
		while(count <= 250 && battery.getCharge() >= 1.25 && !map.goodFeatures.isEmpty()) {
			
			List<Direction> illegalDirections = new ArrayList<>();
			
			//Write latitude and longitude to the file
			txtWriter.print(position.latitude + " ");
			txtWriter.print(position.longitude + " ");
			
			Feature closestFeature = getClosestFeature(map.goodFeatures);
			Direction closestDirection = null;
	
			HashMap<Direction, Double> distances = new HashMap<>();
			
			boolean arrived = false;
			
			while(!arrived) {
				
				for(Direction currentDirection : directions) {
					
					Position next = position.nextPosition(currentDirection);
					double currentDistance = next.getDist(map.getCoordinates(closestFeature));
					
					if(!next.inPlayArea()) {
						illegalDirections.add(currentDirection);
						continue;
					}
					
					for(Feature bf : map.badFeatures) {
						if(next.inRange(next.getDist(map.getCoordinates(bf)))) {
							illegalDirections.add(currentDirection);
							break;
						}
					}
					
					if(!illegalDirections.contains(currentDirection)) {
						distances.put(currentDirection, currentDistance);
						
						if(next.inRange(currentDistance) ) {
							map.goodFeatures.remove(closestFeature);
							arrived = true;
						}
					}
				}
				
				Double closestDistance = Collections.min(distances.values());
				List<Direction> keys = new ArrayList<>(distances.keySet());
				
				for(int i = 0; i < distances.size(); i++) {
					Direction currentDirection = keys.get(i);
					if(distances.get(currentDirection) == closestDistance) {
						closestDirection = currentDirection;
						break;
					}
				}
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
			
			endMove(closestDirection);
		}
		map.writeFlightPath(flightPath, fileName);
	}
	
	// Helper function for everything we need to do once we have decided on a move
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
		System.out.println(count);
		count++;
		
	}
	
	public Feature getClosestFeature(List<Feature> currentFeatures) {
		
		HashMap<Feature, Double> distances = new HashMap<>();
		
		for(int i = 1; i < currentFeatures.size(); i++) {
			double currentDistance = position.getDist(map.getCoordinates(currentFeatures.get(i)));
			Feature currentFeature = currentFeatures.get(i);
			distances.put(currentFeature, currentDistance);
		}
		
		Double closestDistance = Collections.min(distances.values());
		
		/* Check if there are any duplicate features that are the same min distance away
		 * and pick the one that will give us the highest number of coins.
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
			/* Otherwise just return the closest feature
			 */
			return closestFeatures.get(0);
		}
		
	}
}
