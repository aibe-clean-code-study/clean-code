package org.juyb99.pickmecupspring.repository;

import org.juyb99.pickmecupspring.common.repository.SupabaseRepository;
import org.juyb99.pickmecupspring.dto.request.CategoryRequestDTO;
import org.juyb99.pickmecupspring.dto.response.CategoryResponseDTO;
import org.springframework.stereotype.Repository;

import java.net.URLEncoder;
import java.util.List;

@Repository
public class CategoryRepository extends SupabaseRepository<CategoryRequestDTO, CategoryResponseDTO> {
    public CategoryRepository() {
        super("category");
    }

    public List<CategoryResponseDTO> findAll() {
        return super.findAll("select=*&order=id.asc", CategoryResponseDTO.class);
    }

    public CategoryResponseDTO save(CategoryRequestDTO entity) {
        return super.save(entity, CategoryResponseDTO.class);
    }

    public CategoryResponseDTO findByTheme(String theme) {
        return find("theme=eq.%s".formatted(URLEncoder.encode(theme)), CategoryResponseDTO.class);
    }

    public void updatePlayCountByTheme(String theme) {
        callRpc("increment_play_count_by_theme", "category_theme", theme);
    }
}
