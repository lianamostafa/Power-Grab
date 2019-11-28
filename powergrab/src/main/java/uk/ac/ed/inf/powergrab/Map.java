package uk.ac.ed.inf.powergrab;

// Imports
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.List;

import javax.annotation.processing.Generated;

import org.json.simple.JSONArray; 
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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
		
		InputStreamReader isr = new InputStreamReader(is);
		StringBuffer output = new StringBuffer();
		BufferedReader current = new BufferedReader(isr);
		String currentSTR;
	
		try {
			while((currentSTR = current.readLine()) != null) {
				output.append(currentSTR);
			}
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
		
		if(f.getProperty("coins") == null) {
			throw new NullPointerException("Feature " + f + " does not exist.");
			
		}
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
	

	public void writeFlightPath(List<Position> flightPath, String fileName) {
		
		JSONObject newJSON = new JSONObject();
		JSONParser parser = new JSONParser();
		JSONObject oldJSON = new JSONObject();
		
		JSONArray allFeatures = new JSONArray();
		
		JSONObject newFeature = new JSONObject();
		JSONObject geometry = new JSONObject();
		JSONArray totalCoords = new JSONArray();
		
		try {
			
			oldJSON = (JSONObject) parser.parse(mapSource);
			
			newJSON.put("type", "FeatureCollection");
			newJSON.put("date-generated", oldJSON.get("date-generated"));
			
			for(Feature f : features) {
				JSONObject currentFeature = new JSONObject();
				currentFeature.put("type", "Feature");
				
				JSONObject currentProperties = new JSONObject();
				currentProperties.put("id", getID(f));
				currentProperties.put("coins", getCoins(f));
				currentProperties.put("power", getPower(f));
				currentProperties.put("marker-symbol", getMarkerSymbol(f));
				currentProperties.put("marker-color", getMarkerColour(f));
				currentFeature.put("properties", currentProperties);
				
				JSONObject currentGeometry = new JSONObject();
				currentGeometry.put("type", "Point");
				JSONArray currentCoords = new JSONArray();
				currentCoords.add(getCoordinates(f).get(0));
				currentCoords.add(getCoordinates(f).get(1));
				currentGeometry.put("coordinates", currentCoords);
				currentFeature.put("geometry", currentGeometry);
				
				allFeatures.add(currentFeature);
				
 			}
			
			for(Position currentPos : flightPath) {
				JSONArray currentCoords = new JSONArray();
				currentCoords.add(currentPos.longitude);
				currentCoords.add(currentPos.latitude);
				totalCoords.add(currentCoords);
			}
			
			geometry.put("type", "LineString");
			geometry.put("coordinates", totalCoords);
			
			newFeature.put("type", "Feature");
			newFeature.put("properties", new JSONArray());
			newFeature.put("geometry", geometry);
			
			allFeatures.add(newFeature);
			newJSON.put("features", allFeatures);
			
		} catch (ParseException e) {
			System.out.print("features array not found from source");
		}
		
		try(PrintWriter jsonFile = new PrintWriter(fileName+".geojson")){
			jsonFile.write(newJSON.toJSONString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	
	}
	
	
	
	
		
}
