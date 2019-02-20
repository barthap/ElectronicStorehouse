package com.hapex.electrostore.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * Created by barthap on 2019-02-19.
 */
interface BaseDao<T, ID extends Serializable> {
    List<T> findAll();
    Optional<T> findById(ID id);
    void delete(T entity);
    void deleteAll();

    void persist(T entity);
    void update(T entity);

    void beginTransaction();
    void endTransaction();
}
