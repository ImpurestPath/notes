package ru.rvr.notes.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.rvr.notes.entity.hasId;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.lang.reflect.ParameterizedType;
import java.util.List;

@Transactional
public abstract class AbstractPersistenceRepository<I,T extends hasId<I>> implements PersistenceRepository<T> {
    @PersistenceContext
    private EntityManager entityManager;

    private Class<T> typeOfT;

    private Logger log;

    @SuppressWarnings("unchecked")
    public AbstractPersistenceRepository() {
        // Need to get type from generic class to use as argument in getById method
        this.typeOfT = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        this.log = LoggerFactory.getLogger(typeOfT);
    }

    /**
     * Generic method to get entity by id
     * @param id id of entity
     * @return persisted entity
     */
    public T getById(I id){
        return entityManager.find(typeOfT, id);
    }


    /**
     * Find all entities
     *
     * @return all entities
     */
    public List<T> getAll() {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(typeOfT);
        Root<T> noteRoot = query.from(typeOfT);
        CriteriaQuery<T> noteCriteriaQuery = query.select(noteRoot);
        return entityManager.createQuery(noteCriteriaQuery).getResultList();
    }

    /**
     * Creates or updates entity depends on not null id
     *
     * @param entity entity to create or update
     * @return persisted entity
     */
    public T save(T entity) {
        if (entity.getId() == null) {
            return doCreate(entity);
        } else {
            return doUpdate(entity);
        }
    }

    /**
     * Removes only persisted entity
     *
     * @param entity entity to remove
     */
    public void remove(T entity) {
        if (entity.getId() != null) {
            doDelete(entity);
        }
    }

    private T doCreate(T entity){
        entityManager.persist(entity);
        return entity;
    }

    private void doDelete(T entity){
        entityManager.remove(entity);
    }

    private T doUpdate(T entity){
        return entityManager.merge(entity);
    }
}
