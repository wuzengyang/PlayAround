package homeworks;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import homeworks.Item.ItemBuilder;
import homeworks.Item;

public class GoogleMapAPI {
	private static final String API_HOST = "maps.googleapis.com";
	private static final String SEARCH_PATH = "/maps/api/geocode/json";
	private static final String DEFAULT_TERM = "";  // no restriction
	private static final String API_KEY = "AIzaSyBlTyFAsclOf3DimQAumn330SIeh9IAjZI";

	/**
	 * Creates and sends a request to the TicketMaster API by term and location.
	 */
	//public JSONArray search(double lat, double lon, String term) {
	public List<Item> search(double lat, double lon, String term) {

		String url = "https://" + API_HOST + SEARCH_PATH;
		String latlong = lat + "," + lon;
//		if (term == null) {
//			term = DEFAULT_TERM;
//		}
//		term = urlEncodeHelper(term);
		String query = String.format("latlng=%s,%s&key=%s", lat, lon, API_KEY);
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url + "?" + query).openConnection();
			connection.setRequestMethod("GET");
 
			int responseCode = connection.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url + "?" + query);
			System.out.println("Response Code : " + responseCode);
 
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// Extract events array only.
			JSONObject responseJson = new JSONObject(response.toString());
			JSONArray events =  (JSONArray) responseJson.get("status");
			//JSONArray events = (JSONArray) embedded.get("events");
			//return events;
			return getItemList(events);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
 
	private String urlEncodeHelper(String term) {
		try {
			term = java.net.URLEncoder.encode(term, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return term;
	}
 
	private void queryAPI(double lat, double lon) {
		//JSONArray events = search(lat, lon, null);
		List<Item> itemList = search(lat, lon, null);

		try {
//			for (int i = 0; i < events.length(); i++) {
//			    JSONObject event = events.getJSONObject(i);
//				System.out.println(event);
//			}
			
			for (Item item : itemList) {
				JSONObject jsonObject = item.toJSONObject();
				System.out.println(jsonObject);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
 

	/*******************************************************************/
	/**
	 * Helper methods
	 */
	// Convert JSONArray to a list of item objects.
	private List<Item> getItemList(JSONArray events) throws JSONException {
		List<Item> itemList = new ArrayList<>();

		for (int i = 0; i < events.length(); i++) {
			JSONObject event = events.getJSONObject(i);
			ItemBuilder builder = new ItemBuilder();
			builder.setAddress(getStringFieldOrNull(event, "status"));

			JSONObject venue = getVenue(event);
			if (venue != null) {
				if (!venue.isNull("status")) {
					JSONObject address = venue.getJSONObject("status");
					StringBuilder sb = new StringBuilder();
					if (!address.isNull("line1")) {
						sb.append(address.getString("line1"));
					}
					if (!address.isNull("line2")) {
						sb.append(address.getString("line2"));
					}
					if (!address.isNull("line3")) {
						sb.append(address.getString("line3"));
					}
					builder.setAddress(sb.toString());
				}
			}

			// Uses this builder pattern we can freely add fields.
			Item item = builder.build();
			itemList.add(item);
		}

		return itemList;
	}

	private JSONObject getVenue(JSONObject event) throws JSONException {
		if (!event.isNull("status")) {
			JSONObject address = event.getJSONObject("status");
//			if (!embedded.isNull("venues")) {
//				JSONArray venues = embedded.getJSONArray("venues");
//				if (venues.length() >= 1) {
//					return venues.getJSONObject(0);
//				}
//			}
			return address.getJSONObject("status");
		}
		return null;
	}


	private String getStringFieldOrNull(JSONObject event, String field) throws JSONException {
		return event.isNull(field) ? null : event.getString(field);
	}

	
	 /*****************************************************/
	
	
	
	/**
	 * Main entry for sample TicketMaster API requests.
	 */
	public static void main(String[] args) {
		GoogleMapAPI tmApi = new GoogleMapAPI();
		tmApi.queryAPI(40.71, -73.96);
	}
}

