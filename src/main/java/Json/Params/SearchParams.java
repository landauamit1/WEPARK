package Json.Params;

import Entities.Location;
import com.google.gson.annotations.SerializedName;

/**
 * SearchParams
 */
public class SearchParams {
    @SerializedName("currentLocation")
    private Location currentLocation;
    @SerializedName("destination")
    private Location destination;
    @SerializedName("radius")
    private int radius;

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public int getRadius() {
        return radius;
    }

    public Location getDestination() {
        return destination;
    }
}
