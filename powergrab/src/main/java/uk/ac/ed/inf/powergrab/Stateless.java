package uk.ac.ed.inf.powergrab;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import com.mapbox.geojson.Feature;

public class Stateless {
	
	// Current number of moves implemented (MAX 250)
	private int count = 0;
	public int seedNum;
	public double latitude;
	public double longitude;
	
	public String mapString;
	public String fileName;
	public PrintWriter txtWriter;
	
	public Position position;
	public List<Position> flightPath = new ArrayList<>();
	public Battery battery = new Battery();
	public Coins coins = new Coins();
	public Map map;
	public RandomDirectionGenerator rdg;

	
	public Stateless(String mapString, double latitude, double longitude, int seedNum, Position position, String fileName) {
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
		
		rdg  = new RandomDirectionGenerator(seedNum);

	}
	
	public void Move() {
		
		// Add base position to flightPath
		flightPath.add(new Position(position.latitude, position.longitude));
		Direction[] directions = Direction.values();

		
		while(count <= 250 && battery.getCharge() >= 1.25) {
			
			HashMap<Direction, Feature> validDirections = new HashMap<Direction, Feature>();
			List<Direction> illegalDirections = new ArrayList<>();
			
			//Write latitude and longitude to the file
			txtWriter.print(position.latitude + " ");
			txtWriter.print(position.longitude + " ");
			
			for(Direction d : directions) {
				
				Position next = position.nextPosition(d);
				
				if(!next.inPlayArea()) {
					illegalDirections.add(d);
					continue;
				}
				
				for(Feature f : map.features){

					if(map.getPower(f) == 0.0 && map.getCoins(f) == 0.0) {
						continue;
					}
					
					if(next.inRange(next.getDist(map.getCoordinates(f)))) {
						if(map.getMarkerSymbol(f).equals("lighthouse")) {
							validDirections.put(d, f);
							break;
						} else {
							illegalDirections.add(d);
							break;
						}
					}
				}
			}
			
			Object[] bestMove = bestDirection(validDirections, illegalDirections);
			
			Feature bestFeature = (Feature) bestMove[1];
			Position bestPosition = position.nextPosition((Direction) bestMove[0]);
			
			// Write direction of move to the file
			txtWriter.print(bestMove[0] + " ");
			
			// Set our latitude and longitude to match those found in out bestPosition
			position.latitude = bestPosition.latitude;
			position.longitude = bestPosition.longitude;
			
			// Write new latitude and longitude to the file
			txtWriter.print(position.latitude + " ");
			txtWriter.print(position.longitude + " ");
			
			battery.consumeBattery(1.25);
			
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
			txtWriter.print(coins.getCoins() + " ");
			
			// Write new value of battery after move to file
			txtWriter.print(battery.getCharge() + " ");
			// Print new line on file 
			txtWriter.println("");
			
			
			// Add new position to flightPath
			flightPath.add(new Position(position.latitude, position.longitude));
			
			count++;	
			
		}	

		map.writeFlightPath(flightPath, fileName);
	}
	
	public Object[] bestDirection(HashMap<Direction, Feature> moves, List<Direction> illegalDirections) {
		
		Direction bestDirection;
		Object[] result = new Object[2];
		
		
		if(!moves.entrySet().isEmpty()) {
			
			Set<Direction> dirKeySet = moves.keySet();
			List<Direction> dirKeyList = new ArrayList<>(dirKeySet);
			
			bestDirection = dirKeyList.get(0);
			Feature bestFeature = moves.get(bestDirection);
			double bestCoins = map.getCoins(bestFeature);
			double bestPower = map.getPower(bestFeature);

			for(int i = 1; i < moves.size(); i++) {
				Direction currentDirection = dirKeyList.get(i);
				Feature currentFeature = moves.get(currentDirection);
				if(currentFeature != null) {
					double currentCoins = map.getCoins(currentFeature);
					double currentPower = map.getPower(currentFeature);
					
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
				while(illegalDirections.contains(bestDirection)) {
					bestDirection = rdg.getRandomDirection();
				}
			}
			result[0] = bestDirection;
			result[1] = null;
		}
			
		return result;
	}
}
