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
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public List<KeywordSearch> callAPI(NaverSearchParam param) throws Exception {
//        https://developers.naver.com/main/
        String url = "https://openapi.naver.com/v1/search/blog.json";
        String query = URLEncoder.encode(param.query(), StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("%s?query=%s".formatted(url, query)))
                .header("X-Naver-Client-Id", dotenv.get("NAVER_CLIENT_ID"))
                .header("X-Naver-Client-Secret", dotenv.get("NAVER_CLIENT_SECRET"))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();
//        logger.info(responseBody); -> 내용 log 통해 구조 확인 -> NaverSearchResult 구조 정하기
        NaverSearchResult naverSearchResult = objectMapper.readValue(responseBody, NaverSearchResult.class);
        return naverSearchResult.items()
                .stream().map(item -> new KeywordSearch(
                        UUID.randomUUID().toString(), // DB에 들어가기 위한 용이 아니라.. 세션에 저장해놓고 꺼내쓰기 위한 것
                        item.title(),
                        item.link(),
                        item.description(),
                        item.postdate(),
                        "" // DB가 생성해줌
                ))
                .toList();
    }
}
