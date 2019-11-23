package uk.ac.ed.inf.powergrab;

public class App {
	
    public static void main(String[] args){
    	
        int year = Integer.parseInt(args[0]);
        int month = Integer.parseInt(args[1]);
        int day = Integer.parseInt(args[2]);
        
        double startingLong = Double.parseDouble(args[3]);
        double startingLat = Double.parseDouble(args[4]);
        
        int seedNum = Integer.parseInt(args[5]);
        
        String drone = args[6];
        
    }
    
    public String createMapString(int year, int month, int day) {
    	return String.format("http://homepages.inf.ed.ac.uk/stg/powergrab/%d/%d/%d/powergrabmap.geojson", year, month, day);
    }
}


