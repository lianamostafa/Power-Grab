package uk.ac.ed.inf.powergrab;

/* This is a simple class to manage and keep track of
 * our drone's current battery levels.
 */

public class Battery {
	
	// Charge starts at 250.0
	private double charge = 250.0;
	
	
	public void chargeBattery(double amount) {
		charge += amount;
	}
	
	public void consumeBattery(double amount) {
		charge -= amount;
	}
	
	public double getCharge() {
		return charge;
	}
	
}
