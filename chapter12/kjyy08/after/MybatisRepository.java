package com.juyb99.dinorun.repository;

import com.juyb99.dinorun.config.MyBatisConfig;
import org.apache.ibatis.session.SqlSession;

import java.util.List;
import java.util.Optional;

abstract public class MybatisRepository<T> implements CrudRepository<T> {
    @Override
    public List<T> findAll() {
        try (SqlSession session = MyBatisConfig.getSqlSessionFactory().openSession()) {
            CrudRepository<T> scoreRepository = session.getMapper(CrudRepository.class);
            return scoreRepository.findAll();
        }
    }

    @Override
    public Optional<T> findById(Long id) {
        try (SqlSession session = MyBatisConfig.getSqlSessionFactory().openSession()) {
            CrudRepository<T> scoreRepository = session.getMapper(CrudRepository.class);
            return scoreRepository.findById(id);
        }
    }

    @Override
    public Optional<T> save(T entity) {
        try (SqlSession session = MyBatisConfig.getSqlSessionFactory().openSession()) {
            CrudRepository<T> scoreRepository = session.getMapper(CrudRepository.class);
            Optional<T> object = scoreRepository.save(entity);
            session.commit();
            return object;
        }
    }
}
