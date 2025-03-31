package util;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.logging.Level;

public abstract class ApiUtils extends ApiUtilState implements ApiUtil{

    public Optional<String> getApiResponse(String apiUrl, String method, String body) {
        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Accept", "application/json");

            switch (method.toUpperCase()) {
                case "POST" ->
                    requestBuilder.POST(HttpRequest.BodyPublishers.ofString(body != null ? body : ""));

                case "PUT" ->
                    requestBuilder.PUT(HttpRequest.BodyPublishers.ofString(body != null ? body : ""));

                case "DELETE" ->
                    requestBuilder.DELETE();

                case "GET" ->
                    requestBuilder.GET();
            }

            HttpRequest request = requestBuilder.build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new RuntimeException("HTTP Request Failed: " + response.statusCode());
            }

            return Optional.of(response.body());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "예외 상황: ", e);
            return Optional.empty();
        }
    }
}
