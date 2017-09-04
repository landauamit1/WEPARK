package DAL.Repositories;

import DAL.DBAccess;
import Entities.Address;
import com.google.common.collect.ImmutableMap;
import org.hibernate.query.Query;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

/**
 * AddressRepository
 */
@Transactional
public class AddressRepository extends CrudRepositoryImpl<Address, Integer> {
    private static AddressRepository _instance;

    private AddressRepository(DBAccess dbAccess) {
        super(dbAccess);
    }

    public static AddressRepository getInstance() {
        if (_instance == null) {
            _instance = new AddressRepository(DBAccess.GetInstance());
        }

        return _instance;
    }

    public Address findOrCreate(Address toFind) {
        // check if address already exist in db
        // if not create new one and save it to db
        Address address = find(toFind);
        if (address == null) {
            save(toFind);
            return toFind;
        }
        return address;
    }

    public Address find(Address address) {
        List<Address> res = query(ImmutableMap.of(
                "name", address.getName(),
                "locationId", address.getLocation().getLocationId()));
        return res.size() != 0 ? res.get(0) : null;
    }

    @Override
    public List<Address> query(Map<String, Object> fieldsParams) {
        Query query = getSession()
                .createQuery("FROM Address WHERE name=:name AND location.locationId=:locationId")
                .setParameter("name", fieldsParams.get("name"))
                .setParameter("locationId", fieldsParams.get("locationId"));
        return query.list();
    }
}

