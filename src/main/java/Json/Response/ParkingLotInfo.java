package Json.Response;

import Entities.Location;
import groovy.lang.Tuple2;

/**
 * ParkingLotInfo
 */
public class ParkingLotInfo {
    private String name;
    private Location location;
    private String distance;
    private String duration;
    AhuzatHahofParkingLotInfo ahuzatHahofParkingLotInfo;

    public ParkingLotInfo(String name, Location location, Tuple2<String, String> distanceDuration) {
        this.name = name;
        this.location = location;
        this.distance = distanceDuration.getFirst();
        this.duration = distanceDuration.getSecond();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAhuzatHahofParkingLotInfo(AhuzatHahofParkingLotInfo ahuzatHahofParkingLotInfo) {
        this.ahuzatHahofParkingLotInfo = ahuzatHahofParkingLotInfo;
    }

    @Override
    public String toString() {
        return "ParkingLotInfo{" +
                "name='" + name + '\'' +
                ", location=" + location +
                ", distance='" + distance + '\'' +
                ", duration='" + duration + '\'' +
                '}';
    }
}
