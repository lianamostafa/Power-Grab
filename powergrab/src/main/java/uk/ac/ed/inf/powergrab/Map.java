package uk.ac.ed.inf.powergrab;

import java.beans.FeatureDescriptor;
// Imports
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;

public class Map {
	
	private URL mapURL;
	private HttpURLConnection conn;
	private String mapSource;
	public List<Feature> features;
	
	// Set up our connection and get our list of features using GeoJson
	public Map(String mapString) {
		
		conn = mapConn(mapString);
		
		try {
			mapSource = streamToString(conn.getInputStream());
		} catch (IOException e) {
			System.out.println("Cannot connect to server");
		}
		
		features = (FeatureCollection.fromJson(mapSource)).features();
		
	}
	
	// Create a connection labelled 'conn' from a given URL
	public HttpURLConnection mapConn(String mapString) {
		try {
			mapURL = new URL(mapString);
		} catch (MalformedURLException e) {
			System.out.printf("URL: %s is invalid \n", mapString);
		}
	
		try {
			conn = (HttpURLConnection) mapURL.openConnection();
		} catch (java.io.IOException e){
			System.out.println("Cannot connect to server");
		}
		
		return conn;
	}
	
	// Function to convert an InputStream object into a String
	public String streamToString(InputStream is) {
		
		/* Using a combination of Buffered Reader and StringBuilder,		 
		 * we get each line from our Input Stream and append it to our 
		 * String Builder output.
		 */
		
		StringBuilder output = new StringBuilder();
		BufferedReader current = new BufferedReader(new InputStreamReader(is));
		
		try {
			while(current.readLine() != null) {
				output.append(current.readLine());
			}
			current.close();
		} catch (IOException e) {
			System.out.println("Cannot connect to server");
		}
		
		return output.toString();
		
	}
	
	// Getters for the features (pre-flight)
	public List<Double> getCoordinates(Feature f){
		return ((Point) f.geometry()).coordinates();
	}
	
	public Float getCoins(Feature f) {
		return (f.getProperty("coins")).getAsFloat();
	}
	
	public Float getPower(Feature f) {
		return (f.getProperty("power")).getAsFloat();
	}
	
	public String getMarkerSymbol(Feature f) {
		return (f.getProperty("marker-symbol")).getAsString();
	}
	
	public String getMarkerColour(Feature f) {
		return (f.getProperty("marker-color")).getAsString();
	}
	
	public String getID(Feature f) {
		return (f.getProperty("id")).getAsString();
	}
	
	// TODO Implement write to JSON function
//	
//	
//	public JsonObject writeFlightPath(List<Position> flightPath) {
//		
//		JsonObject featureCollection = new JsonObject();
//		
//		featureCollection.addProperty("type", "FeatureCollection");
//		featureCollection.addProperty("date-generated", (FeatureCollection.fromJson(mapSource)).);
//		
//		JsonObject flightPathFeature = new JsonObject();
//		JsonObject geometry = new JsonObject();
//		JsonArray coordsArray = new JsonArray();
//		
//		for(Position currentPos : flightPath) {
//			JsonArray currentCoords = new JsonArray();
//			currentCoords.add(currentPos.longitude);
//			currentCoords.add(currentPos.latitude);
//			coordsArray.add(currentCoords);
//		}
//		
//		geometry.addProperty("type", "LineString");
//		geometry.add("coordinates", coordsArray);
//		
//		flightPathFeature.add("geometry", geometry);
//		
//		for(Feature feature : features) {
//			featureCollection.add("features", feature);;
//		}
//		
//		
//		
//	}
	
	
	
	
		
}
