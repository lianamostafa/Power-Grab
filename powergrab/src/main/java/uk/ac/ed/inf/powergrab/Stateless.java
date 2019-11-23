package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.List;

import com.mapbox.geojson.Feature;

public class Stateless extends Drone {
	
	// Current number of moves implemented (MAX 250)
	private int count;
	private RandomDirectionGenerator rdg;
	private Map map;

	public Stateless(String mapString, double latitude, double longitude, int seedNum, Position position) {
		super(mapString, latitude, longitude, seedNum, position);
		rdg = new RandomDirectionGenerator(seedNum);
		map = new Map(mapString);
	}
	
	public void Move() {
		
		while(count < 250 && battery.getCharge() > 0.0) {
			List<Direction> directions = rdg.getRandomDirections();
			List<Direction> validDirections = new ArrayList<>();
			for(Direction d : directions) {
				if(!position.nextPosition(d).inPlayArea()) {
					continue;
				}
				for(Feature f : map.features) {
//					if()
						//TODO check if in range
						// Get rid of any red bois
				}

			}
			
//			pos.latitude = nextPos.latitude;
//			pos.longitude = nextPos.longitude;
//			battery.consumeBattery(1.25);
//			moves.add(pos);
//			count++;
//		
			
		}
		
	}
	

}
