package uk.ac.ed.inf.powergrab;

public class App {
	
    public static void main(String[] args){
    	
    	// Throw an exception if we have an incorrect number of arguments
    	if(args.length != 7) {
    		throw new IllegalArgumentException("You should input the following 7 arguments: day, month, year, starting latitude, starting longitude, seed number, drone type.");
    	} else {
    		String day = args[0];
	        String month = args[1];
	        String year = args[2];
	        double startingLat = Double.parseDouble(args[3]);
	        double startingLong = Double.parseDouble(args[4]);
	        int seedNum = Integer.parseInt(args[5]);
	        String droneType = args[6].toLowerCase();
	        
	        System.out.print("Start...");
	    	Position position = new Position(startingLat, startingLong);
	    	
	    	// Illegal argument errors
	    	
	    	if(!position.inPlayArea()) {
	    		throw new IllegalArgumentException("Invalid longitude/latitude: not in play area");
	    	}
	    	if(startingLat < 0 || startingLong > 0) {
	    		throw new IllegalArgumentException("Invalid longitude/latitude: latitude is always positive and longitude is always negative");
	    	}
	    	if((Integer.parseInt(day) < 1 || Integer.parseInt(day) > 31) || (Integer.parseInt(month) < 1 || Integer.parseInt(month) > 12)) {
	    		throw new IllegalArgumentException("Invalid date: Please enter in format \" DD MM YYYY \"");
	    	}
	    	if(!(droneType.equals("stateless") || droneType.equals("stateful"))) {
	    		throw new IllegalArgumentException("Invalid Drone: Please choose either \"stateless\" or \"stateful\" for your drone type");
	    	}
	    	
	    	String fileName = createFileString(year, month, day, droneType);
	    	String mapString = createMapString(year, month, day);
	    	
	    	/* I have chosen to output the total moves, coins and battery after each drone is finished
	    	 * so that these outputs can be monitored as necessary.
	    	 */
	    	
	    	if(droneType.equals("stateless")) {
	        	Stateless drone = new Stateless(mapString, startingLat, startingLong, seedNum, position, fileName);
	        	drone.Move();
	        	System.out.print("...Finished");
	        	System.out.println("\nTotal moves: " + drone.getCount());
	        	System.out.println("Total coins: " + drone.coins.getCoins());
	        	System.out.println("Total battery: " + drone.battery.getCharge() + "\n");
	    	} else {
	    		// SeedNum is not necessary for this implementation of Stateful
	        	Stateful drone = new Stateful(mapString, startingLat, startingLong, position, fileName);
	        	drone.Move();
	        	System.out.print("...Finished");
	        	System.out.println("\nTotal moves: " + drone.getCount()); 
	        	System.out.println("Total coins: " + drone.coins.getCoins());
	        	System.out.println("Total battery: " + drone.battery.getCharge() + "\n");
	    	}
	    	
	    	
    	}
    }
   
    // Helper functions to create the necessary strings for our url and file name
    public static String createMapString(String year, String month, String day) {
    	return String.format("http://homepages.inf.ed.ac.uk/stg/powergrab/%s/%s/%s/powergrabmap.geojson", year, month, day);
    }
    
    public static String createFileString(String year, String month, String day, String droneType) {
    	return String.format("%s-%s-%s-%s", droneType.toLowerCase(), day, month, year);

    }
}


