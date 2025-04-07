package org.example.searchbookmark.util;

import org.example.searchbookmark.model.vo.KeywordSearch;
import org.example.searchbookmark.model.vo.NaverSearchParam;
import org.example.searchbookmark.model.vo.NaverSearchResult;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Component
public class NaverSearchAPI implements ObjectMapperMixin, DotenvMixin {
    private final MyLogger logger = new MyLogger(this.getClass().getName());
    private final HttpRequestHelper requestHelper = new HttpRequestHelper();

    public List<KeywordSearch> callAPI(NaverSearchParam param) throws Exception {
        String baseUrl = "https://openapi.naver.com/v1/search/blog.json";
        String query = URLEncoder.encode(param.query(), StandardCharsets.UTF_8);
        String requestUrl = "%s?query=%s".formatted(baseUrl, query);

        Map<String, String> headers = Map.of(
                "X-Naver-Client-Id", dotenv.get("NAVER_CLIENT_ID"),
                "X-Naver-Client-Secret", dotenv.get("NAVER_CLIENT_SECRET")
        );

        String responseBody = requestHelper.get(requestUrl, headers);
        NaverSearchResult result = objectMapper.readValue(responseBody, NaverSearchResult.class);

        return result.items().stream()
                .map(item -> new KeywordSearch(
                        UUID.randomUUID().toString(),
                        item.title(),
                        item.link(),
                        item.description(),
                        item.postdate(),
                        ""
                )).toList();
    }
}
