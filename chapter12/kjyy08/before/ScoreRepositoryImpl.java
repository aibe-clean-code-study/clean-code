package com.juyb99.dinorun.repository;

import com.juyb99.dinorun.config.MyBatisConfig;
import com.juyb99.dinorun.dto.ScoreRequestDTO;
import com.juyb99.dinorun.dto.ScoreResponseDTO;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ScoreRepositoryImpl implements ScoreRepository {

    @Override
    public List<ScoreResponseDTO> findAll() {
        try (SqlSession session = MyBatisConfig.getSqlSessionFactory().openSession()) {
            ScoreRepository scoreRepository = session.getMapper(ScoreRepository.class);
            return scoreRepository.findAll();
        }
    }

    @Override
    public ScoreResponseDTO findById(long id) {
        try (SqlSession session = MyBatisConfig.getSqlSessionFactory().openSession()) {
            ScoreRepository scoreRepository = session.getMapper(ScoreRepository.class);
            return scoreRepository.findById(id);
        }
    }

    @Override
    public int save(ScoreRequestDTO score) {
        try (SqlSession session = MyBatisConfig.getSqlSessionFactory().openSession()) {
            ScoreRepository scoreRepository = session.getMapper(ScoreRepository.class);
            int rows = scoreRepository.save(score);
            session.commit();
            return rows;
        }
    }
}
