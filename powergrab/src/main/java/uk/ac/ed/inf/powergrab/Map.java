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
	public void writeFlightPath(List<Position> flightPath, String fileName) {
		
		JSONObject newJSON = new JSONObject();
		JSONParser parser = new JSONParser();
		JSONArray featuresArray = new JSONArray();
		JSONObject newFeature = new JSONObject();
		try {
			
			newJSON = (JSONObject) parser.parse(mapSource);
			featuresArray = (JSONArray) newJSON.get("features");
			
			newFeature = new JSONObject();
			
			newFeature.put("type", "Feature");
			newFeature.put("properties", new JSONArray());
			
			JSONObject geometry = new JSONObject();
			JSONArray totalCoords = new JSONArray();
			geometry.put("type", "LineString");
			
			for(Position currentPos : flightPath) {
				JSONArray currentCoords = new JSONArray();
				currentCoords.add(currentPos.longitude);
				currentCoords.add(currentPos.latitude);
				totalCoords.add(currentCoords);
				
			}
			
			geometry.put("coordinates", totalCoords);
			newFeature.put("geomtery", geometry);
			featuresArray.add(newFeature);
			
			newJSON.put("features", featuresArray);
			
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
