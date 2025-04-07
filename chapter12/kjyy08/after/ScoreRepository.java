package com.juyb99.dinorun.repository;

import com.juyb99.dinorun.dto.ScoreRequestDTO;
import com.juyb99.dinorun.dto.ScoreResponseDTO;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface ScoreRepository {
    List<ScoreResponseDTO> findAllScore();

    ScoreResponseDTO findOneScore(Long id);

    ScoreResponseDTO saveScore(@RequestBody ScoreRequestDTO score);
}
