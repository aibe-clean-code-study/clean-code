package org.juyb99.pickmecupjsp.repository;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.juyb99.pickmecupjsp.common.Exception.APIException;
import org.juyb99.pickmecupjsp.common.repository.Repository;
import org.juyb99.pickmecupjsp.domain.Category;
import org.juyb99.pickmecupjsp.dto.httpclient.APIClientParam;
import org.juyb99.pickmecupjsp.dto.httpclient.HttpMethod;
import org.juyb99.pickmecupjsp.dto.request.CategoryRequestDTO;
import org.juyb99.pickmecupjsp.util.json.JsonUtil;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class CategoryRepository extends Repository {
    private final String SUPABASE_URL = "https://%s.supabase.co/rest/v1/"
            .formatted(Dotenv.configure().ignoreIfMissing().load().get("SUPABASE_PROJECT_URL"));
    private final String SUPABASE_API_KEY = Dotenv.configure().ignoreIfMissing().load().get("SUPABASE_API_KEY");
    private final String TABLE_NAME = "category";
    private static final Logger logger = Logger.getLogger(CategoryRepository.class.getName());

    private CategoryRepository() {
    }

    @Getter
    private final static CategoryRepository instance = new CategoryRepository();

    public static void main(String[] args) {
        CategoryRepository categoryRepository = CategoryRepository.getInstance();
        Category category = categoryRepository.findByTheme("카리나 입덕 직캠 이상형 월드컵");
        logger.info(category.toString());
//        categoryRepository.updatePlayCountById(category.id());
//        categoryRepository.save(new CategoryRequestDTO("카리나 직캠 월드컵", ItemType.IMAGE, "https://hijjpkutwtscrleugyay.supabase.co/storage/v1/object/public/images/563c7082-ef04-4a49-ac1f-0711b24a7e2e.jpg"));
//        categoryRepository.save(new CategoryRequestDTO("음식", ItemType.IMAGE, "https://hijjpkutwtscrleugyay.supabase.co/storage/v1/object/public/images/theme/62b4104c-d2db-4467-a40f-f5ade2bdaf86.jpg"));
    }

    public List<Category> findAll() {
        String query = "select=*&order=id.asc";
        String response = requestAPI(APIClientParam.builder()
                .url(SUPABASE_URL + TABLE_NAME + "?%s".formatted(query))
                .method(HttpMethod.GET)
                .body("")
                .headers(new String[]{"apikey", SUPABASE_API_KEY, "Authorization", "Bearer %s".formatted(SUPABASE_API_KEY)})
                .build()).orElseThrow(() -> new RuntimeException("Supabase API request failed"));

        return JsonUtil.fromJsonList(response, Category.class);
    }

    public Category findByTheme(String theme) {
        String query = "theme=eq.%s".formatted(URLEncoder.encode(theme, StandardCharsets.UTF_8));
        String response = requestAPI(APIClientParam.builder()
                .url(SUPABASE_URL + TABLE_NAME + "?%s".formatted(query)) // TODO: 하.. supabase 조인 드럽게 복잡하네. 문서 어딨냐 대체...
                .method(HttpMethod.GET)
                .body("")
                .headers(new String[]{"apikey", SUPABASE_API_KEY, "Authorization", "Bearer %s".formatted(SUPABASE_API_KEY)})
                .build()).orElseThrow(() -> new RuntimeException("Supabase API request failed"));

        if (response.equals("[]")) {
            throw new APIException(HttpServletResponse.SC_NOT_FOUND, "Category not found");
        }

        return JsonUtil.fromJsonList(response, Category.class).get(0);
    }

    public Category save(CategoryRequestDTO categoryRequestDTO) {
        String body = JsonUtil.toJson(categoryRequestDTO);
        String response = requestAPI(APIClientParam.builder()
                .url(SUPABASE_URL + TABLE_NAME)
                .method(HttpMethod.POST)
                .body(body)
                .headers(new String[]{"apikey", SUPABASE_API_KEY,
                        "Authorization", "Bearer %s".formatted(SUPABASE_API_KEY),
                        "Content-Type", "application/json",
                        "Prefer", "return=representation"})
                .build()).orElseThrow(() -> new RuntimeException("Supabase API request failed"));
        return JsonUtil.fromJsonList(response, Category.class).get(0);
    }

    public void updatePlayCountById(long id) {
        String body = JsonUtil.toJson(Map.of("item_id", id));
        requestAPI(APIClientParam.builder()
                .url(SUPABASE_URL + "rpc/increment_play_count")
                .method(HttpMethod.POST)
                .body(body)
                .headers(new String[]{
                        "apikey", SUPABASE_API_KEY,
                        "Authorization", "Bearer %s".formatted(SUPABASE_API_KEY),
                        "Content-Type", "application/json"
                })
                .build()).orElseThrow(() -> new RuntimeException("Supabase RPC request failed"));
    }
}
