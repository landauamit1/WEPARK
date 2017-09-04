package Entities;

import javax.persistence.*;

/**
 * Favorite
 */

@Entity
@Table(name = "favorite", schema = "wepark")
public class Favorite {
    @Id
    @GeneratedValue
    @Column(name = "favoriteId")
    private int favoriteId;
    @ManyToOne
    @JoinColumn(name = "addressId")
    private Address address;
    @Column(name = "description")
    private String description;

    public Favorite() {
    }

    public Favorite(Address address, String description) {
        this.address = address;
        this.description = description;
    }

    public Address getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }

    public int getFavoriteId() {
        return favoriteId;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Favorite favorite = (Favorite) o;

        if (favoriteId != favorite.favoriteId) return false;
        if (address != null ? !address.equals(favorite.address) : favorite.address != null) return false;
        return description != null ? description.equals(favorite.description) : favorite.description == null;
    }

    @Override
    public int hashCode() {
        int result = favoriteId;
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }
}
