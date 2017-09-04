package Entities;

import javax.persistence.*;

/**
 * Location Entity
 */
@Entity
@Table(name = "location", schema = "wepark")
public class Location {
    @Id
    @GeneratedValue
    @Column(name = "locationId")
    private Integer locationId;
    @Column(name = "latitude")
    private double latitude;
    @Column(name = "longitude")
    private double longitude;

    public Location() {
    }

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
