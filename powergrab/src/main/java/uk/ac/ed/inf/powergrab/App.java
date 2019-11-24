package uk.ac.ed.inf.powergrab;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class App {
	
    public static void main(String[] args) throws FileNotFoundException{
    	
        int day = Integer.parseInt(args[0]);
        int month = Integer.parseInt(args[1]);
        int year = Integer.parseInt(args[2]);
        double startingLat = Double.parseDouble(args[3]);
        double startingLong = Double.parseDouble(args[4]);
        int seedNum = Integer.parseInt(args[5]);
        String droneType = args[6];
        
    	Position position = new Position(startingLat, startingLong);
    	
    	// Illegal argument errors
    	
    	if(!position.inPlayArea()) {
    		throw new IllegalArgumentException("Invalid longitude/latitude: not in play area");
    	}
    	if(startingLat < 0 || startingLong > 0) {
    		throw new IllegalArgumentException("Invalid longitude/latitude: latitude is always positive and longitude is always negative");
    	}
    	if((day < 1 || day > 31) || (month < 1 || month > 12)) {
    		throw new IllegalArgumentException("Invalid date: Please enter in format \" DD MM YYYY \"");
    	}
    	if(!(droneType.equals("stateless") || droneType.equals("stateful"))) {
    		throw new IllegalArgumentException("Invalid Drone: Please choose either \"stateless\" or \"stateful\" for your drone type");
    	}
    	
    	String fileName = createFileString(year, month, day, droneType);
    	PrintWriter writer = new PrintWriter(fileName);
    	
        
        
        
    }
    
    public String createMapString(int year, int month, int day) {
    	return String.format("http://homepages.inf.ed.ac.uk/stg/powergrab/%d/%d/%d/powergrabmap.geojson", year, month, day);
    }
    
    public static String createFileString(int year, int month, int day, String droneType) {
    	return String.format("%s-%d-%d-%d.txt", droneType.toLowerCase(), day, month, year);

    }
}


