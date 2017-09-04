package BL;

import DAL.Repositories.AddressRepository;
import DAL.Repositories.LocationRepository;
import DAL.Repositories.ParkingSpotRepository;
import DAL.Repositories.UserRepository;
import Entities.*;
import Services.GoogleMapService;
import Services.GoogleMessagingService;
import Helpers.JsonSerializer;
import Json.Params.ReportParams;
import Json.Params.SearchParams;
import Json.Params.UpdateStatusParams;
import Json.Response.ParkingSpotInfo;
import Json.Response.ParkingSpotSearchInfo;
import Json.UserBaseInfo;
import groovy.lang.Tuple2;
import org.json.JSONObject;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ParkingSpotManager
 */
public class ParkingSpotManager {
    public static final int BONUS = 5;

    private final ParkingSpotRepository parkingSpotRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

    private final int DELETE_PARKING_SPOT_REPORTER = 0;
    private final int DELETE_PARKING_SPOT_INTERESTED = 1;
    private final int TAKEN_PARKING_SPOT_REPORTER = 2;
    private final int TAKEN_PARKING_SPOT_INTERESTED = 3;
    private final int NEW_PARKING_SPOT_FAVORITE = 6;
    private final String ADMIN = "מערכת";

    public ParkingSpotManager(ParkingSpotRepository parkingSpotRepository,
                              AddressRepository addressRepository,
                              UserRepository userRepository,
                              LocationRepository locationRepository) {
        this.parkingSpotRepository = parkingSpotRepository;
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
    }

    public List<ParkingSpotInfo> getParkingSpots() {
        return parkingSpotRepository.findAll().stream()
                .map(parkingSpot -> new ParkingSpotInfo(parkingSpot))
                .collect(Collectors.toList());
    }

    public ParkingSpotSearchInfo getParkingSpot(int parkingSpotId, String params) throws Exception {
        // check if parking spot exist in db
        ParkingSpot parkingSpot = parkingSpotRepository.findOne(parkingSpotId);
        if (parkingSpot == null) {
            throw new Exception("חניה [" + parkingSpotId + "] לא קיימת");
        }

        // get distance duration from current location to requested parking spot
        Location currentLocation = JsonSerializer.deserialize(params, Location.class);
        Tuple2<String, String> distanceDuration = GoogleMapService.getDistanceDuration(
                currentLocation,
                parkingSpot.getAddress().getLocation()
        );

        return new ParkingSpotSearchInfo(parkingSpot, distanceDuration.getFirst(), distanceDuration.getSecond());
    }

    public void deleteParkingSpot(int parkingSpotId) throws Exception {
        // check if parking spot exist in db
        ParkingSpot parkingSpot = parkingSpotRepository.findOne(parkingSpotId);
        if (parkingSpot == null) {
            throw new Exception("חניה [" + parkingSpotId + "] לא קיימת");
        }

        // send message to parking spot's reporter announcing it's being deleted
        JSONObject msgObject = new JSONObject();
        msgObject.put("message", "החניה עליה דיווחת נמחקה על ידי מנהל");
        try {
            GoogleMessagingService.sendMessage(parkingSpot.getReporter().getToken(), DELETE_PARKING_SPOT_REPORTER, msgObject);
        } catch (Exception e) {
            System.err.println("Error sending message code " + DELETE_PARKING_SPOT_REPORTER);
        }
        // send message to all interested users announcing parking spot was taken
        // remove interest user and delete parking spot from db
        removeInterestedUsers(parkingSpot, DELETE_PARKING_SPOT_INTERESTED, "החניה בה התענינת נמחקה על ידי מנהל");
    }

    public int report(String params) throws Exception {
        ReportParams reportParams = JsonSerializer.deserialize(params, ReportParams.class);
        // check if reporter user exist in db
        User user = userRepository.findOne(reportParams.getReporter());
        if (user == null) {
            throw new Exception("שם משתמש [" + user.getUserName() + "] לא קיים");
        }

        Location location = locationRepository.findOrCreate(reportParams.getAddress().getLocation());
        Address address = addressRepository.findOrCreate(
                new Address(
                        reportParams.getAddress().getName(),
                        location
                )
        );
        // create new parking spot with matching address and reporter and save it to db
        ParkingSpot parkingSpot = new ParkingSpot(user, reportParams.getAvailableFrom(), address);
        int parkingSpotId = parkingSpotRepository.save(parkingSpot);

        // add bonus score to reporter and update in db
        user.setScore(user.getScore() + BONUS);
        userRepository.saveOrUpdate(user);

        Map<Integer, List<Tuple2<String, String>>> userFavoritesMapByScore = getMatchingUserFavoriteMap(address.getLocation());

        new Thread() {
            public void run() {
                // send message to matching user's announcing a new parking spot is available
                userFavoritesMapByScore.forEach((score, userList) -> {
                    List<String> tokens = userList.stream().map(tuple -> tuple.getFirst()).collect(Collectors.toList());
                    JSONObject msgObject = new JSONObject();
                    msgObject.put("message", "התפנתה חניה ב [" + address.getName() + "] בקרבת מועדף");
                    msgObject.put("parkingSpotId", parkingSpot.getParkingSpotId());
                    GoogleMessagingService.sendGroupMessage(tokens, NEW_PARKING_SPOT_FAVORITE, msgObject);
                    try {
                        Thread.sleep(3000);
                    } catch (Exception e) {
                        System.err.println("Sleep Error");
                    }
                });
            }
        }.start();

        return parkingSpotId;
    }

    public List<ParkingSpotSearchInfo> search(String params) throws Exception {
        SearchParams searchParams = JsonSerializer.deserialize(params, SearchParams.class);
        Location currentLocation = searchParams.getCurrentLocation();
        Location destination = searchParams.getDestination();
        int radius = searchParams.getRadius();

        // find matching parking spots (distance between them to current location <= radius)
        return parkingSpotRepository.findAll()
                .stream()
                .filter(parkingSpot -> {
                    try {
                        return GoogleMapService.getDistance(
                                parkingSpot.getAddress().getLocation(),
                                destination
                        ) <= radius;
                    } catch (Exception e) {
                        return false;
                    }
                })
                .map(parkingSpot -> {
                    try {
                        Tuple2<String, String> distanceDuration = GoogleMapService.getDistanceDuration(
                                currentLocation,
                                parkingSpot.getAddress().getLocation()
                        );
                        return new ParkingSpotSearchInfo(parkingSpot, distanceDuration.getFirst(), distanceDuration.getSecond());
                    } catch (Exception e) {
                        return null;
                    }
                })
                .collect(Collectors.toList());
    }

    public int addInterested(String userName, int parkingSpotId) throws Exception {
        // check if user exist in db
        User user = userRepository.findOne(userName);
        if (user == null) {
            throw new Exception("שם משתמש [" + userName + "] לא קיים");
        }

        // check if parking spot exist in db
        ParkingSpot parkingSpot = parkingSpotRepository.findOne(parkingSpotId);
        if (parkingSpot == null) {
            throw new Exception("חניה [" + parkingSpotId + "] לא קיימת");
        }

        // check is user already interested in another parking spot
        ParkingSpot interestedParkingSpot = user.getInterestedParkingSpot();
        if (interestedParkingSpot != null) {
            return interestedParkingSpot.getParkingSpotId();
        }

        // add interested user to matching parking spot and update in db
        parkingSpot.getInterested().add(user);
        parkingSpotRepository.saveOrUpdate(parkingSpot);
        user.setInterestedParkingSpot(parkingSpot);
        userRepository.saveOrUpdate(user);
        return parkingSpotId;
    }

    public void deleteInterested(String userName) throws Exception {
        // check if user exist in db
        User user = userRepository.findOne(userName);
        if (user == null) {
            throw new Exception("שם משתמש [" + userName + "] לא קיים");
        }

        // remove user from previous interested parking spot
        ParkingSpot previousInterestedParkingSpot = user.getInterestedParkingSpot();
        previousInterestedParkingSpot.getInterested().remove(user);
        parkingSpotRepository.saveOrUpdate(previousInterestedParkingSpot);
        // remove matching parking spot from user.interestedParkingSpot
        user.setInterestedParkingSpot(null);
        userRepository.saveOrUpdate(user);

    }

    public int updateInterested(String userName, int parkingSpotId) throws Exception {
        // check if user exist in db
        User user = userRepository.findOne(userName);
        if (user == null) {
            throw new Exception("שם משתמש [" + userName + "] לא קיים");
        }

        // check if parking spot exist in db
        ParkingSpot parkingSpot = parkingSpotRepository.findOne(parkingSpotId);
        if (parkingSpot == null) {
            throw new Exception("חניה [" + parkingSpotId + "] לא קיימת");
        }

        // remove user from previous interested parking spot
        ParkingSpot previousInterestedParkingSpot = user.getInterestedParkingSpot();
        previousInterestedParkingSpot.getInterested().remove(user);
        parkingSpotRepository.saveOrUpdate(previousInterestedParkingSpot);
        // add user to new parking spot interested list
        parkingSpot.getInterested().add(user);
        parkingSpotRepository.saveOrUpdate(parkingSpot);
        // update user.interestedParkingSpot
        user.setInterestedParkingSpot(parkingSpot);
        userRepository.saveOrUpdate(user);

        return parkingSpotId;
    }

    public List<UserBaseInfo> getInterested(int parkingSpotId) throws Exception {
        ParkingSpot parkingSpot = parkingSpotRepository.findOne(parkingSpotId);
        if (parkingSpot == null) {
            throw new Exception("חניה [" + parkingSpotId + "] לא קיימת");
        }

        return parkingSpot.getInterested().stream()
                .map(user -> new UserBaseInfo(user.getUserName(), user.getLicensePlate(), user.getCarModel()))
                .collect(Collectors.toList());
    }

    public void updateStatus(String params) throws Exception {
        UpdateStatusParams updateStatusParams = JsonSerializer.deserialize(params, UpdateStatusParams.class);
        String userName = updateStatusParams.getUserName();
        int parkingSpotId = updateStatusParams.getParkingSpotId();

        // check if user exist in db
        User user = userRepository.findOne(userName);
        if (user == null) {
            throw new Exception("שם משתמש [" + userName + "] לא קיים");
        }
        // check if parking spot exist in db
        ParkingSpot parkingSpot = parkingSpotRepository.findOne(parkingSpotId);
        if (parkingSpot == null) {
            throw new Exception("חניה [" + parkingSpotId + "] לא קיימת");
        }
        // add bonus score for user
        user.setScore(user.getScore() + BONUS);

        // add bonus score for reporter user
        User reporter = parkingSpot.getReporter();
        reporter.setScore(reporter.getScore() + BONUS);
        userRepository.saveOrUpdate(reporter);

        // send message to reporter announcing parking spot was taken
        JSONObject msgObject = new JSONObject();
        msgObject.put("message", "החניה עליה דיווחת נתפסה");
        try {
            GoogleMessagingService.sendMessage(reporter.getToken(), TAKEN_PARKING_SPOT_REPORTER, msgObject);
        } catch (Exception e) {
            System.err.println("Error sending message code TAKEN_PARKING_SPOT_REPORTER");
        }

        // send message to all interested users announcing parking spot was taken
        // remove interest user and delete parking spot from db
        removeInterestedUsers(parkingSpot, TAKEN_PARKING_SPOT_INTERESTED, "החניה בה התענינת נתפסה");
    }

    private void removeInterestedUsers(ParkingSpot parkingSpot, int code, String message) {
        // send message to all interested users
        List<User> interestedUsers = parkingSpot.getInterested();
        List<String> interestedUserTokens = interestedUsers.stream()
                .map(User::getToken)
                .collect(Collectors.toList());
        if (!interestedUsers.isEmpty()) {
            // send group message to all interested users
            JSONObject msgObject = new JSONObject();
            msgObject.put("message", message);
            GoogleMessagingService.sendGroupMessage(interestedUserTokens, code, msgObject);

            // remove interested parking spot from all interested users
            interestedUsers.forEach(interested -> {
                interested.setInterestedParkingSpot(null);
                userRepository.saveOrUpdate(interested);
            });
        }
        // remove interest user and delete parking spot from db
        parkingSpot.setInterested(null);
        parkingSpotRepository.saveOrUpdate(parkingSpot);
        parkingSpotRepository.delete(parkingSpot);

    }

    private Map<Integer, List<Tuple2<String, String>>> getMatchingUserFavoriteMap(Location location) {
        List<Tuple2<User, String>> userFavoriteList = userRepository.findAll()
                .stream()
                .map(_user -> {
                    Tuple2<User, String> tuple = null;
                    for (Favorite favorite : _user.getFavorites()) {
                        try {
                            if (GoogleMapService.getDistance(
                                    favorite.getAddress().getLocation(),
                                    location) <= 100) {
                                tuple = new Tuple2(_user, favorite.getDescription());
                                break;
                            }
                        } catch (Exception e) {
                            return null;
                        }
                    }
                    return tuple;
                })
                .filter(tuple -> tuple != null)
                .collect(Collectors.toList());

        Map<Integer, List<Tuple2<String, String>>> userFavoriteMapByScore = userFavoriteList
                .stream()
                .collect(Collectors.groupingBy(tuple -> tuple.getFirst().getScore()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        e -> e.getKey(),
                        e -> e.getValue().stream()
                                .map(tuple -> {
                                    return new Tuple2<String, String>(tuple.getFirst().getToken(), tuple.getSecond());
                                })
                                .collect(Collectors.toList()),
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new
                ));


        return userFavoriteMapByScore;
    }

}
