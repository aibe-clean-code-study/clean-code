package com.juyb99.dinorun.repository;

import com.juyb99.dinorun.dto.ScoreRequestDTO;
import com.juyb99.dinorun.dto.ScoreResponseDTO;
import com.juyb99.dinorun.entity.Score;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Qualifier("ScoreMybatisRepository")
public class ScoreRepositoryMybatisImpl extends MybatisRepository<Score> implements ScoreRepository {

    public List<ScoreResponseDTO> findAllScore() {
        List<Score> scores = findAll();
        return scores.stream()
                .map((score) -> new ScoreResponseDTO(score.getScoreId(), score.getNickname(), score.getPoint()))
                .toList();
    }

    public ScoreResponseDTO findOneScore(Long id) {
        Score score = findById(id)
                .orElseThrow(() -> new RuntimeException("Score not found"));
        return new ScoreResponseDTO(score.getScoreId(), score.getNickname(), score.getPoint());
    }

    public ScoreResponseDTO saveScore(ScoreRequestDTO scoreRequestDTO) {
        Score score = new Score(scoreRequestDTO);
        score = save(score)
                .orElseThrow(() -> new RuntimeException("Score not created"));
        return new ScoreResponseDTO(score.getScoreId(), score.getNickname(), score.getPoint());
    }
}
