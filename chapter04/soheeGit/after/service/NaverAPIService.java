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

/**
 * 시스템 환경 변수에서 네이버 API 클라이언트 ID와 클라이언트 시크릿을 가져와 설정합니다.
 * 환경 변수가 설정되지 않은 경우 예외를 발생시킵니다.
 * @throws RuntimeException 환경 변수 {@code NAVER_CLIENT_ID} 또는 {@code NAVER_CLIENT_SECRET}이 설정되지 않은 경우 발생
 */
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
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String, String> body = new HashMap<>();
        NaverAPIClient param = new NaverAPIClient(
                "https://openapi.naver.com/v1/search/book.json?query=%s".formatted(query),
                "GET",
                body,
                "X-Naver-Client-Id", clientID, "X-Naver-Client-Secret", clientSecret
        );
        
        // API 요청 생성 및 실행
        HttpResponse<String> response = httpClient.send(HttpRequest.newBuilder()
                .uri(URI.create(param.getUrl()))
                .method(param.getMethod(), HttpRequest.BodyPublishers.noBody())
                .headers(param.getHeaders().entrySet().stream()
                        .flatMap(entry -> Stream.of(entry.getKey(), entry.getValue()))
                        .toArray(String[]::new))
                .build(), HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());    // TODO: 추후 로깅 시스템 적용 필요
        NaverAPIResult responseBody = objectMapper.readValue(response.body(), NaverAPIResult.class);

        return responseBody.getItems();
    }
}
