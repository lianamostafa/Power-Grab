package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomDirectionGenerator {
	private List<Direction> directionsList;
	private Random rnd;
	
	public RandomDirectionGenerator(int seedNumber, List<Direction> directionsList) {
		this.directionsList = directionsList;
		rnd = new Random(seedNumber);
	}
	
	public Direction getRandomDirection(){
		int i = rnd.nextInt(directionsList.size());
		return directionsList.get(i);

	}
	
}
