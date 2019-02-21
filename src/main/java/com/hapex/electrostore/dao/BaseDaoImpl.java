package com.hapex.electrostore.dao;

import com.hapex.electrostore.util.database.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;

/**
 * Created by barthap on 2019-02-19.
 */
@Slf4j
abstract class BaseDaoImpl<T, ID extends Serializable> implements BaseDao<T, ID> {
    protected EntityManager em;
    protected Class<T> entityClass;

    BaseDaoImpl() {
        em = HibernateUtil.getEntityManager();
        ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        this.entityClass = (Class<T>) genericSuperclass.getActualTypeArguments()[0];
        log.trace("Creating DAO for entity: " + entityClass.getSimpleName());
    }


    @Override
    public List<T> findAll() {
        return em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass)
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(em.find(entityClass, id));
    }

    @Override
    public void delete(T entity) {
        em.remove(entity);
    }

    @Override
    public void deleteAll() {
        //TODO: Not implemented
        throw new NotImplementedException();
    }

    @Override
    public void persist(T entity) {
        em.persist(entity);
    }

    @Override
    public void update(T entity) {
        em.merge(entity);
    }

    @Override
    public void beginTransaction() {
        em.getTransaction().begin();
    }

    @Override
    public void endTransaction() {
        em.getTransaction().commit();
    }
}
