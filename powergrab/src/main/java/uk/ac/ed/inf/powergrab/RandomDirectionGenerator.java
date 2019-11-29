package uk.ac.ed.inf.powergrab;

import java.util.Random;

public class RandomDirectionGenerator {
	private Random rnd;
	
	public RandomDirectionGenerator(int seedNumber) {
		rnd = new Random(seedNumber);
	}
	
	public Direction getRandomDirection(){
		int i = rnd.nextInt(16);
		return Direction.values()[i];

	}
	
}
