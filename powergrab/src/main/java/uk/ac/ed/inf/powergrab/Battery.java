package uk.ac.ed.inf.powergrab;

public class Battery {
	
	// Charge starts at 250.0
	private double charge = 250.0;
	
	/* Here I have made the assumption that 250.0 is the max charge for our battery
	 * as it is unrealistic to assume that the drone can have unlimited power.
	 */
	
	public void chargeBattery(double amount) {
		if(amount + charge > 250.0) {
			charge = 250.0;
		} else {
			charge += amount;
		}
	}
	
	/* I have decided to take a similar approach with discharging the battery
	 * as you cannot have a negative percentage of battery.
	 */
	
	public void consumeBattery(double amount) {
		if(charge - amount < 0) {
			charge = 0.0;
		} else {
			charge -= amount;
		}
	}
	
	public double getCharge() {
		return charge;
	}
	
}
