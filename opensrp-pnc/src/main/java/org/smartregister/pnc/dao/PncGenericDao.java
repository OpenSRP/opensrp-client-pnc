package org.smartregister.pnc.dao;

import java.util.List;

public interface PncGenericDao<T> {

    boolean saveOrUpdate(T t);

    T findOne(T t);

    boolean delete(T t);

    List<T> findAll();
}
