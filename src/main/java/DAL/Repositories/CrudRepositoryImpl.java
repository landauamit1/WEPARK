package DAL.Repositories;

import DAL.DBAccess;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.transaction.Transactional;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
public abstract class CrudRepositoryImpl<T, ID extends Serializable> implements CrudRepository<T, ID> {
    protected final DBAccess dbAccess;
    private final Class<T> typeOfT;

    public CrudRepositoryImpl(DBAccess dbAccess) {
        this.dbAccess = dbAccess;
        this.typeOfT = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
//        Table table = typeOfT.getAnnotation(Table.class);
    }

    @Override
    public ID save(T entity) {
        return ((ID) getSession().save(entity));
    }

    @Override
    public void saveOrUpdate(T entity) {
        getSession().saveOrUpdate(entity);
    }

    protected Session getSession() {
        return dbAccess.getSessionFactory().getCurrentSession();
    }

    @Override
    public List<ID> save(List<T> entities) {
        return entities.stream().map(entity -> save(entity)).collect(Collectors.toList());
    }

    @Override
    public T findOne(ID id) {
        return getSession().get(typeOfT, id);
    }

    @Override
    public List<T> findAll() {
        return getSession().createQuery("from " + typeOfT.getSimpleName()).list();
    }

    @Override
    public List<T> findAll(List<ID> ids) {
        return getSession().byMultipleIds(typeOfT).multiLoad(ids);
    }

    @Override
    public long count() {
        return ((Long) getSession().createQuery("select count(*) from " + typeOfT.getSimpleName()).uniqueResult()).longValue();
    }

    @Override
    public void deleteById(ID id) {
        T entity = findOne(id);
        if (entity != null) {
            delete(entity);
        }
    }

    @Override
    public void delete(T entity) {
        Session session = getSession();
        session.save(entity);
        session.delete(entity);
    }

    @Override
    public void delete(Iterable<? extends T> entities) {
        entities.forEach(e -> delete(e));
    }

    @Override
    public void deleteAll() {
        getSession().createQuery("DELETE FROM " + typeOfT.getSimpleName()).executeUpdate();
    }

    @Override
    public List<T> query(Map<String, Object> fieldsParams) {
        Query query = createQueryByParams(fieldsParams.keySet(), getSession());
        fieldsParams.forEach((col, arg) -> query.setParameter(col, arg));
        return query.list();
    }

    private Query createQueryByParams(Set<String> keySetField, Session session) {
        String query = keySetField.stream().collect(Collectors.mapping(
                es -> es + "=:" + es,
                Collectors.joining(" AND ", "FROM " + typeOfT.getSimpleName() + " WHERE ", "")));
        return session.createQuery(query);
    }

    protected abstract T findOrCreate(T entity);

    protected abstract T find(T entity);
}

