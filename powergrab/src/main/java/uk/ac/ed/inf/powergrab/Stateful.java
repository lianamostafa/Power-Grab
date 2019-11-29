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
	private int count;
	public int seedNum;
	public double latitude;
	public double longitude;
	
	public String mapString;
	public String fileName;
	public PrintWriter txtWriter;
	
	public Position position;
	public List<Position> flightPath = new ArrayList<>();
	public List<Feature> visitedStations = new ArrayList<>();
	public List<Feature> currentFeatures = new ArrayList<>();
	public Battery battery = new Battery();
	public Coins coins = new Coins();
	public Map map;

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
		
		// Add base position to flightPath
		flightPath.add(new Position(position.latitude, position.longitude));
		Direction[] directions = Direction.values();
		
		while(count <= 250 && battery.getCharge() >= 1.25) {
			
		}
		
	}
	
	public Feature closestFeature(List<Feature> currentFeatures) {
		
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
