package DAL.Repositories;

import DAL.DBAccess;
import Entities.ParkingSpot;
import com.google.common.collect.ImmutableMap;
import org.hibernate.query.Query;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

/**
 * ParkingSpotRepository
 */
@Transactional
public class ParkingSpotRepository extends CrudRepositoryImpl<ParkingSpot, Integer> {
    private static ParkingSpotRepository _instance;

    private ParkingSpotRepository(DBAccess dbAccess) {
        super(dbAccess);
    }

    public static ParkingSpotRepository getInstance() {
        if (_instance == null) {
            _instance = new ParkingSpotRepository(DBAccess.GetInstance());
        }

        return _instance;
    }

    public ParkingSpot findOrCreate(ParkingSpot toFind) {
        ParkingSpot parkingSpot = find(toFind);
        if (parkingSpot == null) {
            save(toFind);
            return toFind;
        }
        return parkingSpot;
    }

    public ParkingSpot find(ParkingSpot parkingSpot) {
        List<ParkingSpot> res = query(ImmutableMap.of(
                "availableFrom", parkingSpot.getAddress().getAddressId(),
                "addressId", parkingSpot.getAvailableFrom(),
                "reporter", parkingSpot.getReporter().getUserName()));
        return res.size() != 0 ? res.get(0) : null;
    }

    @Override
    public List<ParkingSpot> query(Map<String, Object> fieldsParams) {
        Query query = getSession()
                .createQuery("FROM ParkingSpot WHERE availableFrom=:availableFrom AND address.addressId=:addressId AND reporter.userName=:reporter")
                .setParameter("availableFrom", fieldsParams.get("availableFrom"))
                .setParameter("addressId", fieldsParams.get("addressId"))
                .setParameter("reporter", fieldsParams.get("reporter"));
        return query.list();
    }
}
