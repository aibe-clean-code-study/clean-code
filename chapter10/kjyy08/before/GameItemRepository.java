package org.juyb99.pickmecupjsp.repository;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import org.juyb99.pickmecupjsp.common.repository.Repository;
import org.juyb99.pickmecupjsp.domain.GameItem;
import org.juyb99.pickmecupjsp.dto.httpclient.APIClientParam;
import org.juyb99.pickmecupjsp.dto.httpclient.HttpMethod;
import org.juyb99.pickmecupjsp.dto.request.GameItemRequestDTO;
import org.juyb99.pickmecupjsp.util.json.JsonUtil;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class GameItemRepository extends Repository {
    private final String SUPABASE_URL = "https://%s.supabase.co/rest/v1/"
            .formatted(Dotenv.configure().ignoreIfMissing().load().get("SUPABASE_PROJECT_URL"));
    private final String SUPABASE_API_KEY = Dotenv.configure().ignoreIfMissing().load().get("SUPABASE_API_KEY");
    private final String TABLE_NAME = "game_item";
    private static final Logger logger = Logger.getLogger(GameItemRepository.class.getName());

    private GameItemRepository() {
    }

    @Getter
    private final static GameItemRepository instance = new GameItemRepository();

    public static void main(String[] args) {
        GameItemRepository gameItemRepository = GameItemRepository.getInstance();

        gameItemRepository.save(new GameItemRequestDTO("[입덕직캠] 에스파 카리나 직캠 4K 'Supernova' (aespa KARINA FanCam) | @MCOUNTDOWN_2024.5.16",
                "카리나 직캠 월드컵",
                "https://www.youtube.com/watch?v=1U2vTeZklbw&ab_channel=M2"));

//        List<GameItem> gameItem = youtubeRepository.findByTitle("tripleS - Girls Never Die");
//        logger.info(gameItem.toString());
//        youtubeRepository.updateTotalWinsById(4);
    }

    public List<GameItem> findAll() {
        String query = "select=*,category(*)";
        String response = requestAPI(APIClientParam.builder()
                .url(SUPABASE_URL + TABLE_NAME + "?%s".formatted(query))
                .method(HttpMethod.GET)
                .body("")
                .headers(new String[]{"apikey", SUPABASE_API_KEY, "Authorization", "Bearer %s".formatted(SUPABASE_API_KEY)})
                .build()).orElseThrow(() -> new RuntimeException("Supabase API request failed"));

        return JsonUtil.fromJsonList(response, GameItem.class);
    }

    public GameItem findById(long id) {
        String query = "id=eq.%s".formatted(id);
        String response = requestAPI(APIClientParam.builder()
                .url(SUPABASE_URL + TABLE_NAME + "?%s".formatted(query))
                .method(HttpMethod.GET)
                .body("")
                .headers(new String[]{"apikey", SUPABASE_API_KEY, "Authorization", "Bearer %s".formatted(SUPABASE_API_KEY)})
                .build()).orElseThrow(() -> new RuntimeException("Supabase API request failed"));

        return JsonUtil.fromJsonList(response, GameItem.class).get(0);
    }

    public List<GameItem> findByTitle(String title) {
        String query = "select=*&title=eq.%s".formatted(URLEncoder.encode(title, StandardCharsets.UTF_8));
        String response = requestAPI(APIClientParam.builder()
                .url(SUPABASE_URL + TABLE_NAME + "?%s".formatted(query))
                .method(HttpMethod.GET)
                .body("")
                .headers(new String[]{"apikey", SUPABASE_API_KEY, "Authorization", "Bearer %s".formatted(SUPABASE_API_KEY)})
                .build()).orElseThrow(() -> new RuntimeException("Supabase API request failed"));

        return JsonUtil.fromJsonList(response, GameItem.class);
    }

    public List<GameItem> findByTheme(String theme) {
        String query = "select=*&category_theme=eq.%s".formatted(URLEncoder.encode(theme, StandardCharsets.UTF_8));
        String response = requestAPI(APIClientParam.builder()
                .url(SUPABASE_URL + TABLE_NAME + "?%s".formatted(query)) // TODO: 하.. supabase 조인 드럽게 복잡하네. 문서 어딨냐 대체...
                .method(HttpMethod.GET)
                .body("")
                .headers(new String[]{"apikey", SUPABASE_API_KEY, "Authorization", "Bearer %s".formatted(SUPABASE_API_KEY)})
                .build()).orElseThrow(() -> new RuntimeException("Supabase API request failed"));

        return JsonUtil.fromJsonList(response, GameItem.class);
    }

    public GameItem save(GameItemRequestDTO gameItemRequestDTO) {
        String body = JsonUtil.toJson(gameItemRequestDTO);
        String response = requestAPI(APIClientParam.builder()
                .url(SUPABASE_URL + TABLE_NAME)
                .method(HttpMethod.POST)
                .body(body)
                .headers(new String[]{"apikey", SUPABASE_API_KEY,
                        "Authorization", "Bearer %s".formatted(SUPABASE_API_KEY),
                        "Content-Type", "application/json",
                        "Prefer", "return=representation"})
                .build()).orElseThrow(() -> new RuntimeException("Supabase API request failed"));
        return JsonUtil.fromJsonList(response, GameItem.class).get(0);
    }

    public void updateTotalWinsById(long id) {
        String body = JsonUtil.toJson(Map.of("item_id", id));
        requestAPI(APIClientParam.builder()
                .url(SUPABASE_URL + "rpc/increment_total_wins")
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
