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

    private String removeHtmlTagsAndEntities(String body){
        body = body.replace("<b>", "");
        body = body.replace("<\\/b>", "");
        body = body.replace("&quot;", "");
        body = body.replace("&amp;", "");
        body = body.replace(" +0900", "");
        return body;
    }

    private HttpResponse<String> sendRequest(APIClientParam param) throws IOException, InterruptedException {
        String body = Optional.of(createClientBody(param))
                .orElseThrow(() -> new RuntimeException("No body found"));

        return httpClient.send(HttpRequest.newBuilder()
                .uri(URI.create(param.url()))
                .method(param.method(), HttpRequest.BodyPublishers.ofString(body))
                .headers(param.headers())
                .build(), HttpResponse.BodyHandlers.ofString());
    }

    private String createClientBody(APIClientParam param) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(param.body());
    }

    private void handleApiError(Exception e) {
        CustomExceptionHandler.handleApiError(e);
    }
}
