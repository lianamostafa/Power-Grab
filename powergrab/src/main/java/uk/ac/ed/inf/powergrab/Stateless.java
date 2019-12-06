package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import com.mapbox.geojson.Feature;

public class Stateless extends Drone{
	
	public int seedNum;
	private RandomDirectionGenerator rdg;
	public List<Position> flightPath = new ArrayList<>();

	// Initialise stateless drone
	public Stateless(String mapString, double latitude, double longitude, int seedNum, Position position, String fileName) {
		super(mapString, latitude, longitude, position, fileName);
		this.seedNum = seedNum;
		rdg  = new RandomDirectionGenerator(seedNum);
	}
	
	public void Move() {
		
		// Add base position to flightPath
		flightPath.add(new Position(position.latitude, position.longitude));
		
		Direction[] directions = Direction.values();

		while(moveCount < 250 && battery.getCharge() >= 1.25) {
			
			HashMap<Direction, Feature> validDirections = new HashMap<Direction, Feature>();
			HashMap<Direction, Feature> badDirections = new HashMap<Direction, Feature>();
			List<Direction> illegalDirections = new ArrayList<>();
			
			
			//Write the old latitude and longitude to the file
			txtWriter.print(position.latitude + " ");
			txtWriter.print(position.longitude + " ");
			
			/* Check the next position for each direction to filter out
			 * any bad directions (those not in the play area or that lead to a bad node).
			 */
			
			for(Direction d : directions) {
				
				Position next = position.nextPosition(d);
				
				if(!next.inPlayArea()) {
					illegalDirections.add(d);
					continue;
				}
				
				for(Feature f : map.features){
					
					/*  If the feature leading in this direction has already been visited,
					 *  skip to the next iteration.
					 */
					
					if(map.getPower(f) == 0.0 && map.getCoins(f) == 0.0) {
						continue;
					}
					
					/* If a feature is in range, check the marker symbol. If it's a
					 * "lighthouse" add to to validDirections, otherwise add it to
					 * illegalDirections.
					 */
					
					if(next.inRange(next.getDist(map.getCoordinates(f)))) {
						if(map.getMarkerSymbol(f).equals("lighthouse")) {
							validDirections.put(d, f);
							break;
						} else {
							illegalDirections.add(d);
							badDirections.put(d, f);
							break;
						}
					}
				}
			}
			
			Object[] bestMove;
			
			/* Once we have filtered out all the illegal moves, pick our best move
			 * using the function bestDirection.
			 *
			 * If the drone is surrounded by bad charging stations,
			 * it picks whichever one that will cause the least harm.
			 */
			
			if(illegalDirections.size() == 16) {
				illegalDirections.removeAll(badDirections.keySet());
				bestMove = bestDirection(badDirections, illegalDirections);
			} else {
				bestMove = bestDirection(validDirections, illegalDirections);
			}
			 
			
			// Get the associated feature and new position
			Feature bestFeature = (Feature) bestMove[1];
			Position bestPosition = position.nextPosition((Direction) bestMove[0]);
			
			// Write direction of move to the file
			txtWriter.print(bestMove[0] + ",");
			
			// Set our latitude and longitude to match those found in out bestPosition
			position.latitude = bestPosition.latitude;
			position.longitude = bestPosition.longitude;
			
			// Write new latitude and longitude to the file
			txtWriter.print(position.latitude + ",");
			txtWriter.print(position.longitude + ",");
			
			battery.consumeBattery(1.25); // Drone has moved, consume battery
			
			/* If we have arrived at a charging station, consume all coins
			 * and charge from the station (and reset these values to 0).
			 */
			
			if(bestFeature != null) {
				battery.chargeBattery(map.getPower(bestFeature));
				coins.addCoins(map.getCoins(bestFeature));
				int bestFeatureIndex = map.features.indexOf(bestFeature);
				map.features.get(bestFeatureIndex).removeProperty("coins");
				map.features.get(bestFeatureIndex).removeProperty("power");
				map.features.get(bestFeatureIndex).addNumberProperty("coins", 0.0);
				map.features.get(bestFeatureIndex).addNumberProperty("power", 0.0);
			}

			// Write new value of coins after move to file
			txtWriter.print(coins.getCoins() + ",");
			
			// Write new value of battery after move to file
			txtWriter.print(battery.getCharge());
			
			if(moveCount != 249) {
				// Print new line on file 
				txtWriter.println("");
			}

			// Add new position to flightPath
			flightPath.add(new Position(position.latitude, position.longitude));
			
			// Move complete, increment moveCount
			moveCount++;	
			
		}	
		txtWriter.close();
		map.writeFlightPath(flightPath, fileName);
	}
	
	
	private Object[] bestDirection(HashMap<Direction, Feature> moves, List<Direction> illegalDirections) {
		
		/* Best direction takes in a HashMap of the current options for valid directions (with their corresponding features),
		 * and decides which direction is the best to go for (in terms of feature value).
		 * 
		 * If the current list of valid directions have no features associated with them,
		 * a random direction out of the 16 possible is then chosen 
		 * (making sure that said direction does not appear on our list of illegal directions).
		 * 
		 * We return an Object array of size 2 with the best direction, and the associated feature 
		 * we can visit (if there is one)
		 */
		
		Direction bestDirection;
		Object[] result = new Object[2];
		
		if(!moves.entrySet().isEmpty()) {
			
			Set<Direction> dirKeySet = moves.keySet();
			List<Direction> dirKeyList = new ArrayList<>(dirKeySet);
			
			// Initialise with the first key/value in moves
			bestDirection = dirKeyList.get(0);
			Feature bestFeature = moves.get(bestDirection);
			double bestCoins = map.getCoins(bestFeature);
			double bestPower = map.getPower(bestFeature);
			
			// Loop through the rest of moves 
			for(int i = 1; i < moves.size(); i++) {
				Direction currentDirection = dirKeyList.get(i);
				Feature currentFeature = moves.get(currentDirection);
				if(currentFeature != null) {
					double currentCoins = map.getCoins(currentFeature);
					double currentPower = map.getPower(currentFeature);
					
					/*  If we find that the current charge is less than 20 (which
					 *  is highly unlikely as we avoid any bad charging stations), 
					 *  we prioritise returning a feature which will give us a higher 
					 *  charge over coins.
					 *  
					 *  Otherwise, we will usually be prioritising returning the feature
					 *  which will maximise our intake of coins.
					 */
					
					if(battery.getCharge() < 20) {
						if(currentPower > bestPower) {
							bestCoins = currentCoins;
							bestPower = currentPower;
							bestDirection = currentDirection;
						}
					} else if(currentCoins > bestCoins) {
						bestCoins = currentCoins;
						bestPower = currentPower;
						bestDirection = currentDirection;
					}
				}
			}
			
			result[0] = bestDirection;
			result[1] = moves.get(bestDirection);
			
		} else {
			
			bestDirection = rdg.getRandomDirection();
			
			if(illegalDirections.contains(bestDirection)) {
				
				/*  Keep picking the next random direction if the one given
				 *  is on our list of illegal directions.
				 */
				
				while(illegalDirections.contains(bestDirection)) {
					bestDirection = rdg.getRandomDirection();
				}
			}
			
			/* As there is no feature associated with the random direction,
			 * we can simply set the second value in our array as null.
			 */
			
			result[0] = bestDirection;
			result[1] = null; 
		}
			
		return result;
	}

}
