package uk.ac.ed.inf.powergrab;

/* This is a simple class to manage and
 * keep track of our drone's current coins.
 */

public class Coins {
	
	// Drone starts with 0 coins
	private double collected = 0.0;
	
	public void addCoins(double amount) {
		collected += amount;
	}
	
	public void removeCoins(double amount) {
		collected -= amount;
	}
	
	public double getCoins() {
		return collected;
	}
}
