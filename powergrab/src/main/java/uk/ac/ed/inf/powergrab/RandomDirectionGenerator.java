package uk.ac.ed.inf.powergrab;

import java.util.Random;

public class RandomDirectionGenerator {
	private Random rnd;
	
	public RandomDirectionGenerator(int seedNumber) {
		rnd = new Random(seedNumber);
	}
	
	public Direction getRandomDirection(){
		
		/* This function returns a random direction out of the 16 possible,
		 * based on the seed number given.
		 */
		
		int i = rnd.nextInt(16);
		return Direction.values()[i];

	}
	
}
