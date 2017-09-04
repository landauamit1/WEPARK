package Services;

import Entities.Location;
import groovy.lang.Tuple2;
import org.json.JSONObject;
import se.walkercrou.places.GooglePlaces;
import se.walkercrou.places.Param;
import se.walkercrou.places.Place;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * GoogleMapService
 */
public class GoogleMapService {
    private static final String MY_API_KEY = "AIzaSyAL-1QLtpDk5d7jqJeDcpKFzNZAakTKtE0";
    private static final String MAP_URL = "https://maps.googleapis.com/maps/api/directions/json?language=iw&origin=%s,%s&destination=%s,%s&key=%s&mode=driving";

    public static Tuple2<String, String> getDistanceDuration(Location from, Location to) throws Exception {
        Tuple2<JSONObject, JSONObject> result = getDirectionsResult(from, to);

        String distance = result.getFirst().getString("text");
        String duration = result.getSecond().getString("text");

        return new Tuple2(distance, duration);
    }

    public static long getDistance(Location from, Location to) throws Exception {
        Tuple2<JSONObject, JSONObject> result = getDirectionsResult(from, to);
        return result.getFirst().getLong("value");
    }

    private static Tuple2<JSONObject, JSONObject> getDirectionsResult(Location from, Location to) throws Exception {
        BufferedReader br = null;

        URL url = new URL(String.format(
                MAP_URL,
                from.getLatitude(),
                from.getLongitude(),
                to.getLatitude(),
                to.getLongitude(),
                MY_API_KEY
        ));

        try {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            JSONObject jsonObject = new JSONObject(sb.toString());
            JSONObject steps = jsonObject
                    .getJSONArray("routes")
                    .getJSONObject(0)
                    .getJSONArray("legs")
                    .getJSONObject(0);

            return new Tuple2(steps.getJSONObject("distance"), steps.getJSONObject("duration"));
        } catch (Exception e) {
            throw new Exception("Error in 'getDirectionsResult': " + e.getMessage());
        } finally {
            br.close();
        }
    }

    public static List<Place> getParkingLots(Location from, int radius) throws Exception {
        try {
            GooglePlaces client = new GooglePlaces(MY_API_KEY);
            return client.getNearbyPlaces(
                    from.getLatitude(),
                    from.getLongitude(),
                    radius,
                    GooglePlaces.MAXIMUM_RESULTS,
                    new Param("language").value("iw"),
                    new Param("types").value("parking")
            );
        } catch (Exception e) {
            System.out.println("ERROR :" + e.getMessage());
            throw e;
        }
    }
}
