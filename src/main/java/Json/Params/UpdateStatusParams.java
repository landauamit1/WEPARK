package Json.Params;

import com.google.gson.annotations.SerializedName;

/**
 * UpdateStatusParams
 */
public class UpdateStatusParams {

    @SerializedName("userName")
    private String userName;
    @SerializedName("parkingSpotId")
    private int parkingSpotId;

    public String getUserName() {
        return userName;
    }

    public int getParkingSpotId() {
        return parkingSpotId;
    }
}
