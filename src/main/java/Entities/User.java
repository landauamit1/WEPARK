package Entities;

import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * User Entity
 */
@Entity
@Table(name = "user", schema = "wepark")
public class User {
    @Id
    @Column(name = "userName")
    private String userName;
    @Column(name = "password")
    private String password;
    @Column(name = "carModel")
    private String carModel;
    @Column(name = "licensePlate")
    private String licensePlate;
    @Column(name = "token")
    private String token;
    @Column(name = "score")
    private int score;
    @Column(name = "active")
    private boolean active;
    @ManyToOne
    @JoinColumn(name = "interestedParkingSpotId", referencedColumnName = "parkingSpotId")
    private ParkingSpot interestedParkingSpot;
    @ManyToMany
    @Cascade(CascadeType.DELETE)
    @JoinTable(name = "user_favorite", joinColumns = {@JoinColumn(name = "userName")}, inverseJoinColumns = {@JoinColumn(name = "favoriteId")})
    private List<Favorite> favorites = new ArrayList<>();

    public User() {
    }

    public User(String userName, String password, String carModel, String licensePlate, String token) {
        this.userName = userName;
        this.password = password;
        this.carModel = carModel;
        this.licensePlate = licensePlate;
        this.score = 0;
        this.active = true;
        this.token = token;
    }

    public ParkingSpot getInterestedParkingSpot() {
        return interestedParkingSpot;
    }

    public void setInterestedParkingSpot(ParkingSpot interestedParkingSpot) {
        this.interestedParkingSpot = interestedParkingSpot;
    }

    public List<Favorite> getFavorites() {
        return favorites;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getCarModel() {
        return carModel;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public int getScore() {
        return score;
    }

    public boolean isActive() {
        return active;
    }

    public void addFavorite(Favorite favorite) {
        favorites.add(favorite);
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (score != user.score) return false;
        if (active != user.active) return false;
        if (userName != null ? !userName.equals(user.userName) : user.userName != null) return false;
        if (password != null ? !password.equals(user.password) : user.password != null) return false;
        if (carModel != null ? !carModel.equals(user.carModel) : user.carModel != null) return false;
        if (licensePlate != null ? !licensePlate.equals(user.licensePlate) : user.licensePlate != null) return false;
        if (interestedParkingSpot != null ? !interestedParkingSpot.equals(user.interestedParkingSpot) : user.interestedParkingSpot != null)
            return false;
        return favorites != null ? favorites.equals(user.favorites) : user.favorites == null;
    }

    @Override
    public int hashCode() {
        int result = userName != null ? userName.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (carModel != null ? carModel.hashCode() : 0);
        result = 31 * result + (licensePlate != null ? licensePlate.hashCode() : 0);
        result = 31 * result + score;
        result = 31 * result + (active ? 1 : 0);
        result = 31 * result + (interestedParkingSpot != null ? interestedParkingSpot.hashCode() : 0);
        result = 31 * result + (favorites != null ? favorites.hashCode() : 0);
        return result;
    }
}
