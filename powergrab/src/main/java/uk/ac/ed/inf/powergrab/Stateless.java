package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import com.mapbox.geojson.Feature;
import java.io.PrintWriter;

public class Stateless extends Drone {
	
	// Current number of moves implemented (MAX 250)
	private int count;
	private RandomDirectionGenerator rdg;
	private Map map;
	public List<Position> totalMoves = new ArrayList<Position>();

	public Stateless(String mapString, double latitude, double longitude, int seedNum, Position position, PrintWriter writer) {
		super(mapString, latitude, longitude, seedNum, position, writer);
		rdg = new RandomDirectionGenerator(seedNum);
		map = new Map(mapString);
	}
	
	public void Move() {
		
		// Add base position to totalMoves
		totalMoves.add(position);
		
		while(count < 250 && battery.getCharge() >= 1.25) {
			
			List<Direction> directions = rdg.getRandomDirections();
			HashMap<Direction, Feature> validDirections = new HashMap<Direction, Feature>();
			HashMap<Direction, Feature> dangerDirections = new HashMap<Direction, Feature>();
			
			//Write latitude and longitude to the file
			writer.print(position.latitude + " ");
			writer.print(position.longitude + " ");
			
			for(int i = 0; i < directions.size(); i++) {
				
				Direction d = directions.get(i);
				Position next = position.nextPosition(d);
				boolean isDanger = false;
				
				if(!next.inPlayArea()) {
					continue;
				}
				for(Feature f : map.features) {
					// Check if feature is in range
					if(next.inRange(map.getCoordinates(f))) {
						if(map.getMarkerColour(f) == "lighthouse") {
							validDirections.put(d, f);
							break;
						} else {
							isDanger = true;
							dangerDirections.put(d, f);
							break;
						}
					}
				}
				
				if(isDanger != true && !validDirections.containsKey(d)) {
					validDirections.put(d, null);
				}

			}
			
			Object[] best;
			
			if(!validDirections.equals(null)) {
				best = bestDirection(validDirections);
			} else {
				best = bestDirection(dangerDirections);
			}
			
			Feature bestFeature = (Feature) best[1];
			Position bestPosition = position.nextPosition((Direction) best[0]);
			
			// Write direction of move to the file
			writer.print(best[0] + " ");
			
			// Set our latitude and longitude to match those found in out bestPosition
			position.latitude = bestPosition.latitude;
			position.longitude = bestPosition.longitude;
			
			// Write new latitude and longitude to the file
			writer.print(position.latitude + " ");
			writer.print(position.longitude + " ");
			
			battery.consumeBattery(1.25);
			battery.chargeBattery(map.getPower(bestFeature));
			coins.addCoins(map.getCoins(bestFeature));
			
			// Write new value of coins after move to file
			writer.print(map.getCoins(bestFeature) + " ");
			
			// Write new value of battery after move to file
			writer.print(map.getPower(bestFeature) + " ");
			// Print new line on file 
			writer.println("");
			
			// Add new position to totalMoves
			totalMoves.add(position);
			
			count++;	
			
		}
		
	}
	
	public Object[] bestDirection(HashMap<Direction, Feature> directions) {
		
		Set<Direction> keySet = directions.keySet();
		Direction[] keyArr = (Direction[]) keySet.toArray();
		
		double bestCoins = map.getCoins(directions.get(keyArr[0]));
		double bestPower = map.getPower(directions.get(keyArr[0]));
		
		Direction bestDirection = keyArr[0];
		
		if(directions.size() > 1) {
			for(int i = 1; i < directions.size(); i++) {
				Feature current = directions.get(keyArr[i]);
				if(current != null) {
					double currentCoins = map.getCoins(current);
					double currentPower = map.getPower(current);
					
					if(battery.getCharge() < 20) {
						if(currentPower > bestPower) {
							bestCoins = currentCoins;
							bestPower = currentPower;
							bestDirection = keyArr[i];
						}
					} else if(coins.getCoins() < 400) {
						if(currentCoins > bestCoins) {
							bestCoins = currentCoins;
							bestPower = currentPower;
							bestDirection = keyArr[i];
						}
					} else if(currentPower > bestPower || currentCoins > bestCoins) {
						bestCoins = currentCoins;
						bestPower = currentPower;
						bestDirection = keyArr[i];
					}
				}
			}
		}
		
		Object[] result = {bestDirection, directions.get(bestDirection)};	
		return result;
	}
	
	

}
