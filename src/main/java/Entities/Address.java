package Entities;

import javax.persistence.*;

/**
 * Address Entity
 */

@Entity
@Table(name = "address", schema = "wepark")
public class Address {
    @Id
    @GeneratedValue
    @Column(name = "addressId")
    private int addressId;
    @Column(name = "name")
    private String name;
    @ManyToOne
    @JoinColumn(name = "locationId")
    private Location location;

    public Address(){}

    public Address(String name, Location location) {
        this.name = name;
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public int getAddressId() {
        return addressId;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Address address = (Address) o;

        if (addressId != address.addressId) return false;
        if (name != null ? !name.equals(address.name) : address.name != null) return false;
        return location != null ? location.equals(address.location) : address.location == null;
    }

    @Override
    public int hashCode() {
        int result = addressId;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        return result;
    }
}
