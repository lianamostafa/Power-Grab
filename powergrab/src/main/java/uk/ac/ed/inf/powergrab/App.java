package uk.ac.ed.inf.powergrab;

public class App {
	
    public static void main(String[] args){
    	
    	if(args.length != 7) {
    		throw new IllegalArgumentException("You should input the following 7 arguments: day, month, year, starting latitude, sttarting longitude, seed number, drone type.");
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
	    	
	    	if(droneType.equals("stateless")) {
	        	Stateless drone = new Stateless(mapString, startingLat, startingLong, seedNum, position, fileName);
	        	drone.Move();
	        	System.out.print("...Finished");
	        	System.out.println("\nTotal coins: " + drone.coins.getCoins());
	        	System.out.println("Total battery: " + drone.battery.getCharge() + "\n");
	    	} else {
	        	Stateful drone = new Stateful(mapString, startingLat, startingLong, seedNum, position, fileName);
	        	drone.Move();
	        	System.out.print("...Finished");
	        	System.out.println("\nTotal coins: " + drone.coins.getCoins());
	        	System.out.println("Total battery: " + drone.battery.getCharge() + "\n");
	    	}
	    	
	    	
    	}
    }
   
    public static String createMapString(String year, String month, String day) {
    	return String.format("http://homepages.inf.ed.ac.uk/stg/powergrab/%s/%s/%s/powergrabmap.geojson", year, month, day);
    }
    
    public static String createFileString(String year, String month, String day, String droneType) {
    	return String.format("%s-%s-%s-%s", droneType.toLowerCase(), day, month, year);

    }
}


