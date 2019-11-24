package uk.ac.ed.inf.powergrab;

public class Coins {
	
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
