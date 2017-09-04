package BL;

import DAL.Repositories.*;
import Entities.*;
import Helpers.JsonSerializer;
import Json.Params.InterestedParkingSpotParams;
import Json.Params.MessageParams;
import Json.Response.ParkingSpotSearchInfo;
import Json.Response.UserPersonalInfo;
import Json.UserBaseInfo;
import Services.GoogleMapService;
import Services.GoogleMessagingService;
import groovy.lang.Tuple2;
import org.json.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

/**
 * UserManager
 */
public class UserManager {
    private final ParkingSpotRepository parkingSpotRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final FavoriteRepository favoriteRepository;

    private final int DELETE_USER = 4;
    private final int MESSAGE = 5;

    public UserManager(ParkingSpotRepository parkingSpotRepository,
                       AddressRepository addressRepository,
                       UserRepository userRepository,
                       LocationRepository locationRepository,
                       FavoriteRepository favoriteRepository) {
        this.parkingSpotRepository = parkingSpotRepository;
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
        this.favoriteRepository = favoriteRepository;
    }

    public List<UserBaseInfo> getUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserBaseInfo(user.getUserName(), user.getLicensePlate(), user.getCarModel()))
                .collect(Collectors.toList());
    }

    public void deleteUser(String userName) throws Exception {
        User user = userRepository.findOne(userName);
        if (user == null) {
            throw new Exception("שם משתמש [" + userName + "] לא קיים");
        }
        // send message to user announcing he's being deleted
        JSONObject msgObject = new JSONObject();
        msgObject.put("message", "חשבונך נמחק על ידי מנהל");
        try {
            GoogleMessagingService.sendMessage(user.getToken(), DELETE_USER, msgObject);
        } catch (Exception e) {
            System.err.println("Error sending message code " + DELETE_USER);
        }

        try {
            // remove user from interested parking spot if exist
            ParkingSpot interestedParkingSpot = user.getInterestedParkingSpot();
            if (interestedParkingSpot != null) {
                interestedParkingSpot.getInterested().remove(user);
                parkingSpotRepository.saveOrUpdate(interestedParkingSpot);
            }
            // delete user from db
            userRepository.deleteById(userName);
        } catch (Exception e) {
            throw new Exception("שגיאה במחיקת משתמש [ " + userName + " ] אנא נסה שנית");
        }
    }

    public List<Favorite> getFavorites(String userName) throws Exception {
        // check if user exist in db
        User user = userRepository.findOne(userName);
        if (user != null) {
            return user.getFavorites();
        } else
            throw new Exception("שם משתמש [" + userName + "] לא קיים");
    }

    public void addFavorites(String userName, String favoriteStr) throws Exception {
        Favorite favorite = JsonSerializer.deserialize(favoriteStr, Favorite.class);

        // check if user exist in db
        User user = userRepository.findOne(userName);
        if (user != null) {
            // save new favorite in db
            Location location = locationRepository.findOrCreate(favorite.getAddress().getLocation());
            Address address = addressRepository.findOrCreate(
                    new Address(
                            favorite.getAddress().getName(),
                            location
                    )
            );
            favorite = favoriteRepository.findOrCreate(new Favorite(address, favorite.getDescription()));

            // add favorite to user's favorites list and update in db
            user.addFavorite(favorite);
            userRepository.saveOrUpdate(user);
        } else
            throw new Exception("שם משתמש [" + userName + "] לא קיים");
    }

    public void updateFavorites(String userName, String favoriteStr) throws Exception {
        Favorite updateFavorite = JsonSerializer.deserialize(favoriteStr, Favorite.class);
        int favoriteId = updateFavorite.getFavoriteId();

        // check if user exist in db
        User user = userRepository.findOne(userName);
        if (user != null) {
            // check if favorite exist in db
            Favorite favorite = favoriteRepository.findOne(favoriteId);
            if (favorite != null) {
                // update favorite details
                Location updateLocation = locationRepository.findOrCreate(updateFavorite.getAddress().getLocation());
                Address updateAddress = addressRepository.findOrCreate(
                        new Address(
                                updateFavorite.getAddress().getName(),
                                updateLocation
                        )
                );

                favorite.setAddress(updateAddress);
                favorite.setDescription(updateFavorite.getDescription());

                // update favorite in db
                favoriteRepository.saveOrUpdate(favorite);
            } else
                throw new Exception("מועדף [" + favoriteId + "] לא קיים");
        } else
            throw new Exception("שם משתמש [" + userName + "] לא קיים");
    }

    public void deleteFavorites(String userName, int favoriteId) throws Exception {
        // check if user exist in db
        User user = userRepository.findOne(userName);
        if (user != null) {
            // check if favorite exist in db
            Favorite favorite = favoriteRepository.findOne(favoriteId);
            if (favorite != null) {
                // remove favorite from user's favorites list and update in db
                user.getFavorites().remove(favorite);
                userRepository.saveOrUpdate(user);
            } else
                throw new Exception("מועדף [" + favoriteId + "] לא קיים");
        } else
            throw new Exception("שם משתמש [" + userName + "] לא קיים");
    }

    public UserPersonalInfo getPersonalInfo(String userName) throws Exception {
        // check if user exist in db
        User user = userRepository.findOne(userName);
        if (user != null) {
            return new UserPersonalInfo(
                    userName,
                    user.getLicensePlate(),
                    user.getCarModel(),
                    user.getPassword(),
                    user.getScore()
            );
        } else
            throw new Exception("שם משתמש [" + userName + "] לא קיים");
    }

    public void updatePersonalInfo(String params) throws Exception {
        UserPersonalInfo personalInfoParams = JsonSerializer.deserialize(params, UserPersonalInfo.class);
        String userName = personalInfoParams.getName();

        // check if user exist in db
        User user = userRepository.findOne(userName);
        if (user != null) {
            // update user details
            user.setCarModel(personalInfoParams.getCarModel());
            user.setLicensePlate(personalInfoParams.getLicensePlate());
            user.setPassword(personalInfoParams.getPassword());

            // update user in db
            userRepository.saveOrUpdate(user);
        } else
            throw new Exception("שם משתמש [" + userName + "] לא קיים");
    }

    public ParkingSpotSearchInfo getInterestedParkingSpot(String params) throws Exception {
        InterestedParkingSpotParams interestedParams = JsonSerializer.deserialize(params, InterestedParkingSpotParams.class);
        String userName = interestedParams.getUserName();
        Location currentLocation = interestedParams.getCurrentLocation();

        // check if user exist in db
        User user = userRepository.findOne(userName);
        if (user != null) {
            // check if user is interested in any parking spot
            ParkingSpot interestedParkingSpot = user.getInterestedParkingSpot();
            if (interestedParkingSpot != null) {
                try {
                    // calculating distance and duration from current location to interested parking spot
                    Tuple2<String, String> distanceDuration = GoogleMapService.getDistanceDuration(
                            currentLocation,
                            interestedParkingSpot.getAddress().getLocation()
                    );
                    return new ParkingSpotSearchInfo(interestedParkingSpot, distanceDuration.getFirst(), distanceDuration.getSecond());
                } catch (Exception e) {
                    throw e;
                }
            } else
                throw new Exception("אינך מעונין בחניה");
        } else
            throw new Exception("שם משתמש [" + userName + "] לא קיים");
    }

    public void sendMessage(String params) throws Exception {
        MessageParams messageParams = JsonSerializer.deserialize(params, MessageParams.class);
        String fromUserName = messageParams.getFromUser();
        String toUserName = messageParams.getToUser();
        String message = messageParams.getText();

        User fromUser = userRepository.findOne(fromUserName);
        if (fromUser == null) {
            throw new Exception("שם משתמש [" + fromUserName + "] לא קיים");
        }

        User toUser = userRepository.findOne(toUserName);
        if (toUser == null) {
            throw new Exception("שם משתמש [" + toUserName + "] לא קיים");
        }

        JSONObject msgObject = new JSONObject();
        msgObject.put("message", message);
        msgObject.put("sender", fromUserName);
        try {
            GoogleMessagingService.sendMessage(toUser.getToken(), MESSAGE, msgObject);
        } catch (Exception e) {
            throw e;
        }
    }
}
