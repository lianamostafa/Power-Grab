package uk.ac.ed.inf.powergrab;

public class Battery {
	
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
