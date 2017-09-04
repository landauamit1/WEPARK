package DAL.Repositories;

import DAL.DBAccess;
import Entities.User;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;

/**
 * UserRepository
 */
@Transactional
public class UserRepository extends CrudRepositoryImpl<User, String> {
    private static UserRepository _instance;

    private UserRepository(DBAccess dbAccess) {
        super(dbAccess);
    }

    public static UserRepository getInstance() {
        if (_instance == null) {
            _instance = new UserRepository(DBAccess.GetInstance());
        }

        return _instance;
    }

    public boolean validate(String userName, String password) {
        EntityManager entityManager = dbAccess.getEntityManagerFactory().createEntityManager();
        boolean validate = false;
        try {
            Object o = entityManager
                    .createQuery("SELECT u FROM User u WHERE u.password =:password AND u.userName=:userName")
                    .setParameter("password", password)
                    .setParameter("userName", userName)
                    .getSingleResult();
            validate = true;
        } catch (NoResultException e) {
            validate = false;
        } finally {
            entityManager.close();
        }
        return validate;
    }

    public User findOrCreate(User toFind) {
        User user = find(toFind);
        if (user == null) {
            save(toFind);
            return toFind;
        }
        return user;
    }

    public User find(User user) {
        return findOne(user.getUserName());
    }

}
