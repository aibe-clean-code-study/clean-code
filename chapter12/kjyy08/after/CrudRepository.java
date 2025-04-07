package com.juyb99.dinorun.repository;

import java.util.List;
import java.util.Optional;


public interface CrudRepository<T> {
    List<T> findAll();

    Optional<T> findById(Long id);

    Optional<T> save(T entity);
}
