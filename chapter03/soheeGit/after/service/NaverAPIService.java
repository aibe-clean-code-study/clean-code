package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.NaverAPIClient;
import org.example.model.NaverAPIResult;
import org.example.model.NaverAPIResultItem;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class NaverAPIService {
    private final String clientID;
    private final String clientSecret;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public NaverAPIService() {
        this.clientID = System.getenv("NAVER_CLIENT_ID");
        this.clientSecret = System.getenv("NAVER_CLIENT_SECRET");
        if (clientID == null || clientSecret == null) {
            throw new RuntimeException("NaverSearchAPI: clientID or clientSecret are missing");
        }
    }

    public List<NaverAPIResultItem> searchByQuery(String query) throws IOException, InterruptedException {
        // https://developers.naver.com/docs/serviceapi/search/book/book.md#%EC%B1%85
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String, String> body = new HashMap<>();
        NaverAPIClient param = new NaverAPIClient(
                "https://openapi.naver.com/v1/search/book.json?query=%s".formatted(query),
                "GET",
                body,
                "X-Naver-Client-Id", clientID, "X-Naver-Client-Secret", clientSecret
        );
        HttpResponse<String> response = httpClient.send(HttpRequest.newBuilder()
                .uri(URI.create(param.getUrl()))
                .method(param.getMethod(), HttpRequest.BodyPublishers.noBody())
                .headers(param.getHeaders().entrySet().stream()
                        .flatMap(entry -> Stream.of(entry.getKey(), entry.getValue()))
                        .toArray(String[]::new))
                .build(), HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        NaverAPIResult responseBody = objectMapper.readValue(response.body(), NaverAPIResult.class);

        // 검색 결과 목록 반환
        return responseBody.getItems();
    }
}
