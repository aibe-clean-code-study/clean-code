package com.juyb99.dinorun.repository;

import com.juyb99.dinorun.dto.ScoreRequestDTO;
import com.juyb99.dinorun.dto.ScoreResponseDTO;

import java.util.List;

public interface ScoreRepository {
    List<ScoreResponseDTO> findAll();

    ScoreResponseDTO findById(long id);

    int save(ScoreRequestDTO score);
}
