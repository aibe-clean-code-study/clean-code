package com.juyb99.dinorun.repository;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class JpaRepository<T> implements CrudRepository<T> {
    @PersistenceContext
    private EntityManager em;

    private final Class<T> entityClass;

    public JpaRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public Optional<T> find(String jpql, Map<String, Object> params) {
        TypedQuery<T> query = em.createQuery(jpql, entityClass);
        params.forEach(query::setParameter);
        return Optional.of(query.getSingleResult());
    }

    public Optional<T> findById(Long id) {
        return Optional.of(em.find(entityClass, id));
    }

    public List<T> findAll() {
        return em.createQuery("select e from " + getEntityName(entityClass) + " e", entityClass).getResultList();
    }

    public List<T> findAll(String jpql, Map<String, Object> params) {
        TypedQuery<T> query = em.createQuery(jpql, entityClass);
        params.forEach(query::setParameter);
        return query.getResultList();
    }

    public Optional<T> save(T entity) {
        em.persist(entity);
        return Optional.of(entity);
    }

    public void delete(Long id) {
        em.remove(em.find(entityClass, id));
    }

    public void delete(T entity) {
        em.remove(em.merge(entity));
    }

    private String getEntityName(Class<?> clazz) {
        Entity entity = clazz.getAnnotation(Entity.class);
        if (entity != null) {
            return entity.name();
        } else {
            return clazz.getSimpleName();
        }
    }

}
