package org.juyb99.pickmecupspring.repository;

import org.juyb99.pickmecupspring.common.repository.SupabaseRepository;
import org.juyb99.pickmecupspring.dto.request.GameItemRequestDTO;
import org.juyb99.pickmecupspring.dto.response.GameItemResponseDTO;
import org.springframework.stereotype.Repository;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Repository
public class GameItemRepository extends SupabaseRepository<GameItemRequestDTO, GameItemResponseDTO> {
    public GameItemRepository() {
        super("game_item");
    }

    public List<GameItemResponseDTO> findAll() {
        return super.findAll("select=*&order=id.asc", GameItemResponseDTO.class);
    }

    public GameItemResponseDTO save(GameItemRequestDTO entity) {
        return super.save(entity, GameItemResponseDTO.class);
    }

    public GameItemResponseDTO findById(long id) {
        return find("id=eq.%s".formatted(id), GameItemResponseDTO.class);
    }

    public List<GameItemResponseDTO> findByTitle(String title) {
        return super.findAll("select=*&title=eq.%s".formatted(URLEncoder.encode(title, StandardCharsets.UTF_8)), GameItemResponseDTO.class);
    }

    public List<GameItemResponseDTO> findByTheme(String theme) {
        return super.findAll("select=*&category_theme=eq.%s".formatted(URLEncoder.encode(theme, StandardCharsets.UTF_8)), GameItemResponseDTO.class);
    }

    public void updateTotalWinsById(long id) {
        callRpc("increment_total_wins", "item_id", id);
    }
}
