package soheeGit.after.webClient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class WebClientImpl implements WebClient {
    public static final Logger logger = Logger.getLogger(WebClientImpl.class.getName());
    public static final HttpClient httpClient = HttpClient.newBuilder().build();

    public void setLoggerLevel(Level level) {
        logger.setLevel(level);
    }

    @Override
    public String sendRequest(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        logger.info(response.statusCode() + "");
        logger.info(body);
        return body;
    }

    @Override
    public HttpRequest makeRequest(String path, HttpMethod method, String body, String... headers) {
        return HttpRequest.newBuilder().uri(URI.create(path))
                .method(method.name, HttpRequest.BodyPublishers.ofString(body))
                .headers(headers)
                .build();
    }
}

interface WebClient {
    String sendRequest(HttpRequest request) throws IOException, InterruptedException;

    HttpRequest makeRequest(String path, HttpMethod method, String body, String... headers);

    enum HttpMethod {
        GET("GET"), POST("POST");
        final String name;

        HttpMethod(String method) {
            this.name = method;
        }
    }
}