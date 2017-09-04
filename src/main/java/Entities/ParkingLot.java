package Entities;

import javax.persistence.*;

/**
 * ParkingLot
 */
@Entity
@Table(name = "parkinglot", schema = "wepark")
public class ParkingLot {
    @Id
    @Column(name = "parkingLotId")
    private int parkingLotId;
    @Column(name = "name")
    private String name;
    @ManyToOne
    @JoinColumn(name = "locationId")
    private Location location;

    public int getParkingLotId() {
        return parkingLotId;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }
}
