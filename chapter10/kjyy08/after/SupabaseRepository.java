package org.juyb99.pickmecupspring.common.repository;

import io.github.cdimascio.dotenv.Dotenv;
import org.juyb99.pickmecupspring.common.util.httpclient.APIClientParam;
import org.juyb99.pickmecupspring.common.util.httpclient.RestAPIRepository;
import org.juyb99.pickmecupspring.common.util.json.JsonUtil;
import org.springframework.http.HttpMethod;

import java.util.List;
import java.util.Map;

public abstract class SupabaseRepository<T, R> extends RestAPIRepository {
    private final String SUPABASE_URL = "https://%s.supabase.co/rest/v1/"
            .formatted(Dotenv.configure().ignoreIfMissing().load().get("SUPABASE_PROJECT_URL"));
    private final String SUPABASE_API_KEY = Dotenv.configure().ignoreIfMissing().load().get("SUPABASE_API_KEY");
    private final String TABLE_NAME;

    public SupabaseRepository(String tableName) {
        this.TABLE_NAME = tableName;
    }

    protected R find(String query, Class<R> clazz) {
        String response = requestAPI(APIClientParam.builder()
                .url(SUPABASE_URL + TABLE_NAME + "?%s".formatted(query))
                .method(HttpMethod.GET)
                .body("")
                .headers(new String[]{"apikey", SUPABASE_API_KEY, "Authorization", "Bearer %s".formatted(SUPABASE_API_KEY)})
                .build()).orElseThrow(() -> new RuntimeException("Supabase API request failed"));

        return JsonUtil.fromJsonList(response, clazz).get(0);
    }

    public List<R> findAll(Class<R> clazz) {
        return findAll("select=*", clazz);
    }

    protected List<R> findAll(String query, Class<R> clazz) {
        String response = requestAPI(APIClientParam.builder()
                .url(SUPABASE_URL + TABLE_NAME + "?%s".formatted(query))
                .method(HttpMethod.GET)
                .body("")
                .headers(new String[]{"apikey", SUPABASE_API_KEY, "Authorization", "Bearer %s".formatted(SUPABASE_API_KEY)})
                .build()).orElseThrow(() -> new RuntimeException("Supabase API request failed"));

        return JsonUtil.fromJsonList(response, clazz);
    }

    public R save(T entity, Class<R> clazz) {
        String body = JsonUtil.toJson(entity);
        String response = requestAPI(APIClientParam.builder()
                .url(SUPABASE_URL + TABLE_NAME)
                .method(HttpMethod.POST)
                .body(body)
                .headers(new String[]{"apikey", SUPABASE_API_KEY,
                        "Authorization", "Bearer %s".formatted(SUPABASE_API_KEY),
                        "Content-Type", "application/json",
                        "Prefer", "return=representation"})
                .build()).orElseThrow(() -> new RuntimeException("Supabase API request failed"));
        return JsonUtil.fromJsonList(response, clazz).get(0);
    }

    public void callRpc(String functionName, String key, Object value) {
        String body = JsonUtil.toJson(Map.of(key, value));
        requestAPI(APIClientParam.builder()
                .url(SUPABASE_URL + "rpc/%s".formatted(functionName))
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
