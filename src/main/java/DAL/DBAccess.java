package DAL;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.reflections.Reflections;

import javax.persistence.Entity;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Set;

public class DBAccess {
    private static DBAccess _instance = new DBAccess();
    private EntityManagerFactory entityManagerFactory;
    private SessionFactory sessionFactory = buildSessionFactory();

    public static DBAccess GetInstance() {
        return _instance;
    }

    private DBAccess() {
        try {
            entityManagerFactory = Persistence.createEntityManagerFactory("WEPARK");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("couldnt create new instance of DBAccess: " + e.getMessage());
        }
        System.out.println("new DBAccess has been initialized");
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public static SessionFactory buildSessionFactory() {
        try {
            // create SessionFactory from hibernate.cfg.xml
            Configuration configuration = new Configuration().configure();
            Reflections reflections = new Reflections("Entities");
            Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(Entity.class);
            for (Class<?> cls : typesAnnotatedWith) {
                configuration.addAnnotatedClass(cls);
            }
            return configuration.buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
