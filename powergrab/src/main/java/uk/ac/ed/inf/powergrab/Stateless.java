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
	private int count;
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
	
	public List<Feature> visitedStations = new ArrayList<>();
	
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
		
		

	}
	
	public void Move() {
		
		// Add base position to flightPath
		flightPath.add(new Position(position.latitude, position.longitude));
		Direction[] directions = Direction.values();
		
		while(count <= 250 && battery.getCharge() >= 1.25) {
			
			HashMap<Direction, Feature> validDirections = new HashMap<Direction, Feature>();
			HashMap<Direction, Feature> dangerDirections = new HashMap<Direction, Feature>();
			
			//Write latitude and longitude to the file
			txtWriter.print(position.latitude + " ");
			txtWriter.print(position.longitude + " ");
			
			for(int i = 0; i < 16; i++) {
				
				Direction d = directions[i];
				Position next = position.nextPosition(d);
				
				if(!next.inPlayArea()) {
					continue;
				}
				
				Double closestDist = null;
				String closestMarker = null;
				Feature closestFeature = null;
				
				for(Feature f : map.features) {
					
					if(visitedStations.contains(f)) {
						break;
					}
					
					Double currentDist = next.getDist(map.getCoordinates(f));
					
					if(currentDist != null) {
						String currentMarker = map.getMarkerSymbol(f);
						if(closestDist == null && closestMarker == null) {
							closestDist = currentDist; 
							closestMarker = currentMarker;
							closestFeature = f;
							continue;
						}
						
						if(currentDist < closestDist) {
							closestDist = currentDist;
							closestMarker = currentMarker;
							closestFeature = f;
						} else if(currentDist == closestDist) {
							if(closestMarker.equals("danger")) {
								closestDist = currentDist;
								closestMarker = currentMarker;
								closestFeature = f;
							}
						}
					}
					
				}
				
				if(closestMarker != null) {
					if(closestMarker.equals("lighthouse")) {
						validDirections.put(d, closestFeature);
					} else if(closestMarker.equals("danger")) {
						dangerDirections.put(d, closestFeature);
					}
				}

				if(!(dangerDirections.containsKey(d)) && !(validDirections.containsKey(d))) {
					validDirections.put(d, null);
				}
				
			}
			
			Object[] bestMove;
			
			if(!validDirections.isEmpty()) {
				System.out.println(count);
				bestMove = bestDirection(validDirections);
			} else {
				System.out.println(count);
				bestMove = bestDirection(dangerDirections);
			}
			
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
				visitedStations.add(bestFeature);
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
	
	public Object[] bestDirection(HashMap<Direction, Feature> moves) {
		Set<Direction> dirKeySet = moves.keySet();
		List<Direction> dirKeyList = new ArrayList<>(dirKeySet);
		
		if(count == 24 || count == 25 || count == 26) {
			for(Direction d : dirKeyList) {
				System.out.println(d);
			}
		}
		
		RandomDirectionGenerator rdg  = new RandomDirectionGenerator(seedNum, dirKeyList);
		
		double bestCoins = 0;
		double bestPower = 0;
		
		if(moves.get(dirKeyList.get(0)) != null) {
			bestCoins = map.getCoins(moves.get(dirKeyList.get(0)));
			bestPower = map.getPower(moves.get(dirKeyList.get(0)));
		}
		
		Direction bestDirection = dirKeyList.get(0);
		
		if(moves.size() > 1) {
			for(int i = 1; i < moves.size(); i++) {
				Feature currentFeature = moves.get(dirKeyList.get(i));
				if(currentFeature != null) {
					double currentCoins = map.getCoins(currentFeature);
					double currentPower = map.getPower(currentFeature);
					
					if(battery.getCharge() < 20) {
						if(currentPower > bestPower) {
							bestCoins = currentCoins;
							bestPower = currentPower;
							bestDirection = dirKeyList.get(i);
						}
					} else if(currentCoins > bestCoins) {
						bestCoins = currentCoins;
						bestPower = currentPower;
						bestDirection = dirKeyList.get(i);
					}
				}
			}
		}
		
		if(bestCoins == 0) {
			bestDirection = rdg.getRandomDirection();
		}
		
		System.out.println("Best direction is: " + bestDirection);
		System.out.println("__________________");
		Object[] result = {bestDirection, moves.get(bestDirection)};	
		return result;
	}
}
