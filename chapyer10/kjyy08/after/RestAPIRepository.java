package org.juyb99.pickmecupspring.common.util.httpclient;

import lombok.extern.slf4j.Slf4j;
import org.juyb99.pickmecupspring.common.Exception.APIException;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

@Slf4j
public abstract class RestAPIRepository {
    protected final HttpClient httpClient;

    protected RestAPIRepository() {
        this.httpClient = HttpClient.newBuilder().build();
        log.info("Initializing API client");
    }

    /**
     * API 호출을 수행하고 응답 본문을 반환합니다.
     *
     * @param param API 요청 파라미터
     * @return API 응답 본문
     */
    protected Optional<String> requestAPI(APIClientParam param) {
        log.info("Calling API client");
        log.info("Request url: {}", param.url());
        HttpResponse<String> response;
        try {
            response = sendRequest(param);
            log.info("\nstatusCode: {}", response.statusCode());
            log.info("\nuri: {}", response.uri());
            log.info("\nbody: {}", response.body());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        int statusCode = response.statusCode();
        if (statusCode >= 400) {
            throw new APIException(HttpStatus.valueOf(statusCode), response.body());
        }

        return Optional.of(response.body());
    }

    /**
     * HTTP 요청을 생성하고 전송합니다.
     *
     * @param param API 요청 파라미터 객체
     * @return HTTP 응답 객체
     * @throws IOException, InterruptedException 전송 중 발생하는 예외
     */
    protected HttpResponse<String> sendRequest(APIClientParam param) throws IOException, InterruptedException {
        HttpRequest.BodyPublisher bodyPublisher;

        if (param.body() instanceof byte[]) {
            bodyPublisher = HttpRequest.BodyPublishers.ofByteArray((byte[]) param.body());
        } else if (param.body() instanceof String) {
            bodyPublisher = HttpRequest.BodyPublishers.ofString((String) param.body());
        } else {
            throw new IllegalArgumentException("Unsupported request body type");
        }

        return httpClient.send(HttpRequest.newBuilder()
                .uri(URI.create(param.url()))
                .method(param.method().name(), bodyPublisher)
                .headers(param.headers())
                .build(), HttpResponse.BodyHandlers.ofString());
    }
}
