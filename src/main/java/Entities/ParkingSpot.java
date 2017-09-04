package Entities;

import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Parking Spot Entity
 */

@Entity
@Table(name = "parkingspot", schema = "wepark")
public class ParkingSpot {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "parkingSpotId")
    private int parkingSpotId;
    @ManyToOne
    @JoinColumn(name = "reporter")
    private User reporter;
    @Column(name = "availableFrom")
    private Date availableFrom;
    @ManyToOne
    @JoinColumn(name = "addressId")
    private Address address;
    @OneToMany
    @Cascade(CascadeType.DELETE)
    @JoinTable(name = "parkingspot_interested", joinColumns = {@JoinColumn(name = "parkingSpotId")}, inverseJoinColumns = {@JoinColumn(name = "userName")})
    private List<User> interested = new ArrayList();

    public ParkingSpot(){
    }

    public ParkingSpot(User reporter, Date availableFrom, Address address ){
        this.reporter = reporter;
        this.availableFrom = availableFrom;
        this.address = address;
    }

    public int getParkingSpotId() {
        return parkingSpotId;
    }

    public void addInterestedUser(User user){
        interested.add(user);
    }

    public Date getAvailableFrom() {
        return availableFrom;
    }

    public Address getAddress() {return this.address;}

    public List<User> getInterested() {
        return interested;
    }

    public User getReporter() {
        return reporter;
    }

    public void setInterested(List<User> interested) {
        this.interested = interested;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParkingSpot that = (ParkingSpot) o;

        if (parkingSpotId != that.parkingSpotId) return false;
        if (reporter != null ? !reporter.equals(that.reporter) : that.reporter != null) return false;
        if (availableFrom != null ? !availableFrom.equals(that.availableFrom) : that.availableFrom != null)
            return false;
        if (address != null ? !address.equals(that.address) : that.address != null) return false;
        return interested != null ? interested.equals(that.interested) : that.interested == null;
    }

    @Override
    public int hashCode() {
        int result = parkingSpotId;
        result = 31 * result + (reporter != null ? reporter.hashCode() : 0);
        result = 31 * result + (availableFrom != null ? availableFrom.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (interested != null ? interested.hashCode() : 0);
        return result;
    }
}
