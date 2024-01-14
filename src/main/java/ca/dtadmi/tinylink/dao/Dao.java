package ca.dtadmi.tinylink.dao;

import java.util.Collection;
import java.util.Optional;

public interface Dao<T> {

    Optional<T> get(String id);
    Collection<T> getAll();
    T save(T t);
    void delete(String id);
    void deleteAll();
}