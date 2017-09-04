package Json.Response;

import Entities.ParkingSpot;

/**
 * ParkingSpotSearchInfo
 */
public class ParkingSpotSearchInfo extends ParkingSpotInfo{
    private String distance;
    private String duration;

    public ParkingSpotSearchInfo(ParkingSpot parkingSpot, String distance, String duration) {
        super(parkingSpot);
        this.distance = distance;
        this.duration = duration;
    }
}

