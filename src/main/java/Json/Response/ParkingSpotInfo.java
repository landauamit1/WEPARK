package Json.Response;

import Entities.Address;
import Entities.ParkingSpot;
import Entities.User;
import Json.UserBaseInfo;

import java.util.Date;

/**
 * ParkingSpotInfo
 */
public class ParkingSpotInfo {
    private int id;
    private Date availableFrom;
    private Address address;
    private int interestedCount;
    private UserBaseInfo user;

    public ParkingSpotInfo(ParkingSpot parkingSpot) {
        this.id = parkingSpot.getParkingSpotId();
        this.availableFrom = parkingSpot.getAvailableFrom();
        this.address = parkingSpot.getAddress();
        this.interestedCount = parkingSpot.getInterested().size();
        User user = parkingSpot.getReporter();
        this.user = new UserBaseInfo(user.getUserName(), user.getLicensePlate(), user.getCarModel());
    }

    public int getId() {
        return id;
    }

    public Address getAddress() {
        return address;
    }
}
