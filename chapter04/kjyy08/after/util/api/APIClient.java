package util.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import exception.handler.CustomExceptionHandler;
import model.dto.APIClientParam;
import util.logger.CustomLogger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

public class APIClient {
    private final CustomLogger logger;
    private final HttpClient httpClient;

    public APIClient() {
        this.logger = new CustomLogger(this.getClass());
        this.httpClient = HttpClient.newBuilder().build();
        logger.info("Initializing API client");
    }

    /**
     * API 호출을 수행하고 불필요한 값을 제외해서 반환합니다.
     * @param param API 요청 파라미터
     * @return API 응답 본문 (HTML 태그 및 일부 엔터티 제거 후 반환)
     */
    public String callAPI(APIClientParam param){
        logger.info("Calling API client");
        HttpResponse<String> response;

        try {
            response = sendRequest(param);
            logger.info("statusCode: %d".formatted(response.statusCode()));
        } catch (IOException | InterruptedException e) {
            handleApiError(e);
            return null;
        }

        return removeHtmlTagsAndEntities(response.body());
    }

    /**
     * HTTP 요청을 생성하고 전송합니다.
     * @param param API 요청 파라미터 객체
     * @return HTTP 응답 객체
     */
    private HttpResponse<String> sendRequest(APIClientParam param) throws IOException, InterruptedException {
        String body = Optional.of(createClientBody(param))
                .orElseThrow(() -> new RuntimeException("No body found"));

        return httpClient.send(HttpRequest.newBuilder()
                .uri(URI.create(param.url()))
                .method(param.method(), HttpRequest.BodyPublishers.ofString(body))
                .headers(param.headers())
                .build(), HttpResponse.BodyHandlers.ofString());
    }

    /**
     * API 요청 본문을 JSON 문자열로 변환합니다.
     * @param param API 요청 파라미터 객체
     * @return 변환된 JSON 문자열
     */
    private String createClientBody(APIClientParam param) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(param.body());
    }

    /**
     * API 호출 중 발생한 예외를 처리합니다.
     */
    private void handleApiError(Exception e) {
        CustomExceptionHandler.handleApiError(e);
    }

    /**
     * API 응답 본문에서 불필요한 응답 값을 제거합니다.
     * @param body API 응답 본문
     * @return 필터된 문자열
     */
    private String removeHtmlTagsAndEntities(String body){
        body = body.replace("<b>", "");
        body = body.replace("<\\/b>", "");
        body = body.replace("&quot;", "");
        body = body.replace("&amp;", "");
        body = body.replace(" +0900", "");
        return body;
    }
}
