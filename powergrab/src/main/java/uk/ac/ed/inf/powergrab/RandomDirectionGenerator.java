package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomDirectionGenerator {
	private int seedNumber;
	private Random rnd;
	
	public RandomDirectionGenerator(int seedNumber) {
		this.seedNumber = seedNumber;
	}
	
	public List<Direction> getRandomDirections(){
		
		rnd = new Random(seedNumber);
		
		List<Direction> randDirections= new ArrayList<>();
		
		for(int i = 0; i < 16; i++) {
			int currentRand = rnd.nextInt(16);
			randDirections.add(Direction.values()[currentRand]);
		}
		
		return randDirections;
	}
	
	
}
