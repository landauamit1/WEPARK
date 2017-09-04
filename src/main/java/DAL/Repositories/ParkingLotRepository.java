package DAL.Repositories;

import DAL.DBAccess;
import Entities.ParkingLot;
import com.google.common.collect.ImmutableMap;
import org.hibernate.query.Query;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

/**
 * ParkingLotRepository
 */
@Transactional
public class ParkingLotRepository extends CrudRepositoryImpl<ParkingLot, Integer> {
    private static ParkingLotRepository _instance;

    private ParkingLotRepository(DBAccess dbAccess) {
        super(dbAccess);
    }

    public static ParkingLotRepository getInstance() {
        if (_instance == null) {
            _instance = new ParkingLotRepository(DBAccess.GetInstance());
        }

        return _instance;
    }

    public ParkingLot findOrCreate(ParkingLot toFind) {
        ParkingLot parkingLot = find(toFind);
        if (parkingLot == null) {
            save(toFind);
            return toFind;
        }
        return parkingLot;
    }

    public ParkingLot find(ParkingLot parkingLot) {
        List<ParkingLot> res = query(ImmutableMap.of(
                "name", parkingLot.getName(),
                "locationId", parkingLot.getLocation().getLocationId()));
        return res.size() != 0 ? res.get(0) : null;
    }

    @Override
    public List<ParkingLot> query(Map<String, Object> fieldsParams) {
        Query query = getSession()
                .createQuery("FROM ParkingLot WHERE name=:name AND location.locationId=:locationId")
                .setParameter("name", fieldsParams.get("name"))
                .setParameter("locationId", fieldsParams.get("locationId"));
        return query.list();
    }

    public ParkingLot findByLocation(int locationId) {
        Query query = getSession()
                .createQuery("FROM ParkingLot WHERE location.id=:locationId")
                .setParameter("locationId", locationId);

        List res = query.list();
        return !res.isEmpty() ? (ParkingLot) query.list().get(0) : null;
    }
}
