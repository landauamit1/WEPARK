package DAL.Repositories;

import DAL.DBAccess;
import Entities.Location;
import com.google.common.collect.ImmutableMap;

import javax.transaction.Transactional;
import java.util.List;

/**
 * LocationRepository
 */
@Transactional
public class LocationRepository extends CrudRepositoryImpl<Location, Integer> {
    private static LocationRepository _instance;

    private LocationRepository(DBAccess dbAccess) {
        super(dbAccess);
    }

    public static LocationRepository getInstance() {
        if (_instance == null) {
            _instance = new LocationRepository(DBAccess.GetInstance());
        }

        return _instance;
    }

    public Location findOrCreate(Location toFind) {
        // checks if location already exist in db
        // if not create new one and save it to db
        Location location = find(toFind);
        if(location == null){
            save(toFind);
            return toFind;
        }
        return location;
    }

    public Location find(Location location) {
        List<Location> res = query(ImmutableMap.of(
                "latitude", location.getLatitude(),
                "longitude", location.getLongitude()));
        return res.size() != 0 ? res.get(0) : null;
    }
}
