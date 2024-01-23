package ca.dtadmi.tinylink.dao;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface Dao<T> {

    Optional<T> findById(String id);
    Collection<T> findAll();
    T save(T t);
    void deleteAllById(List<String> ids);
    void deleteAll();
}