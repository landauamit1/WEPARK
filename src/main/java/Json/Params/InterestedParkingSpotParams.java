package Json.Params;

import Entities.Location;
import com.google.gson.annotations.SerializedName;

/**
 * InterestedParkingSpotParams
 */
public class InterestedParkingSpotParams {
    @SerializedName("userName")
    private String userName;
    @SerializedName("currentLocation")
    private Location currentLocation;

    public String getUserName() {
        return userName;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }
}


