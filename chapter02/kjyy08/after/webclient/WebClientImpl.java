package kjyy08.after.webclient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebClientImpl implements WebClient {
    private final Logger logger;
    private final HttpClient httpClient;

    public WebClientImpl() {
        this.logger = Logger.getLogger(WebClientImpl.class.getName());
        this.httpClient = HttpClient.newBuilder().build();
    }

    public void setLoggerLevel(Level level) {
        logger.setLevel(level);
    }

    @Override
    public String sendRequest(HttpRequest request) {
        HttpResponse<String> response;

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        String body = response.body();
        logger.info(response.statusCode() + "");
        logger.info(body);
        return response.body();
    }

    @Override
    public HttpRequest makeRequest(String path, HttpMethod method, String body, String... headers) {
        return HttpRequest.newBuilder().uri(URI.create(path))
                .method(method.name, HttpRequest.BodyPublishers.ofString(body))
                .headers(headers)
                .build();
    }
}
