package com.juyb99.dinorun.service;

import com.juyb99.dinorun.dto.ScoreRequestDTO;
import com.juyb99.dinorun.dto.ScoreResponseDTO;
import com.juyb99.dinorun.repository.ScoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class ScoreService {
    private final ScoreRepository scoreRepository;

    public ScoreService(ScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
    }

    public List<ScoreResponseDTO> findAllScore() {
        return scoreRepository.findAll();
    }

    public ScoreResponseDTO findOneScore(long id) {
        return scoreRepository.findById(id);
    }

    public int saveScore(@RequestBody ScoreRequestDTO score) {
        return scoreRepository.save(score);
    }
}
