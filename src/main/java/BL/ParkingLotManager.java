package BL;

import DAL.Repositories.LocationRepository;
import DAL.Repositories.ParkingLotRepository;
import Entities.Location;
import Entities.ParkingLot;
import Services.GoogleMapService;
import Services.HtmlParserService;
import Helpers.JsonSerializer;
import Json.Params.SearchParams;
import Json.Response.ParkingLotInfo;
import groovy.lang.Tuple2;
import se.walkercrou.places.Place;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ParkingLotManager
 */
public class ParkingLotManager {

    private final ParkingLotRepository parkingLotRepository;
    private final LocationRepository locationRepository;

    public ParkingLotManager(ParkingLotRepository parkingLotRepository,
                             LocationRepository locationRepository) {
        this.parkingLotRepository = parkingLotRepository;
        this.locationRepository = locationRepository;
    }

    public List<ParkingLotInfo> search(String params) throws Exception {
        SearchParams searchParams = JsonSerializer.deserialize(params, SearchParams.class);
        Location currentLocation = searchParams.getCurrentLocation();
        Location destination = searchParams.getDestination();
        int radius = searchParams.getRadius();

        // find matching parking lots (distance between them to current location <= radius)
        return GoogleMapService.getParkingLots(destination, radius)
                .stream()
                .map(parkingLot -> new Tuple2<Place, Location>(
                        parkingLot,
                        new Location(parkingLot.getLatitude(), parkingLot.getLongitude())
                        )
                )
                .collect(Collectors.toList())
                .stream()
                .map(parkingLotTuple -> {
                    try {
                        // create ParkingLotInfo
                        ParkingLotInfo parkingLotInfo = new ParkingLotInfo(
                                parkingLotTuple.getFirst().getName(),
                                parkingLotTuple.getSecond(),
                                GoogleMapService.getDistanceDuration(currentLocation, parkingLotTuple.getSecond())
                        );

                        // check if parkingLot is AhuzatHafotParkingLot
                        Location location = locationRepository.find(parkingLotTuple.getSecond());
                        if (location != null) {
                            ParkingLot ahuzatHafofParkingLot = parkingLotRepository.findByLocation(location.getLocationId());
                            if (ahuzatHafofParkingLot != null) {
                                parkingLotInfo.setName(ahuzatHafofParkingLot.getName());
                                parkingLotInfo.setAhuzatHahofParkingLotInfo(
                                        HtmlParserService.getParkingLotInfo(ahuzatHafofParkingLot.getParkingLotId())
                                );
                            }
                        }
                        return parkingLotInfo;
                    } catch (Exception e) {
                        return null;
                    }
                })
                .collect(Collectors.toList());
    }
}
