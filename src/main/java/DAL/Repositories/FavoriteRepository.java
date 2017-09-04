package DAL.Repositories;

import DAL.DBAccess;
import Entities.Favorite;
import com.google.common.collect.ImmutableMap;

import javax.transaction.Transactional;
import java.util.List;

/**
 * FavoriteRepository
 */
@Transactional
public class FavoriteRepository extends CrudRepositoryImpl<Favorite, Integer> {
    private static FavoriteRepository _instance;

    private FavoriteRepository(DBAccess dbAccess) {
        super(dbAccess);
    }

    public static FavoriteRepository getInstance() {
        if (_instance == null) {
            _instance = new FavoriteRepository(DBAccess.GetInstance());
        }

        return _instance;
    }

    public Favorite findOrCreate(Favorite toFind){
        // check if address already exist in db
        // if not create new one and save it to db
        Favorite favorite = find(toFind);
        if (favorite == null) {
            save(toFind);
            return toFind;
        }
        return favorite;
    }

    public Favorite find(Favorite favorite){
        List<Favorite> res = query(ImmutableMap.of(
                "addressId", favorite.getAddress().getAddressId(),
                "description", favorite.getDescription()));
        return res.size() != 0 ? res.get(0) : null;
    }
}
