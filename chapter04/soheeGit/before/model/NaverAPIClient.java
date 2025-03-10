package org.example.model;

import java.util.HashMap;
import java.util.Map;

public class NaverAPIClient {
    private String url;
    private String method;
    private Map<String, String> body;
    private Map<String, String> headers;

    public NaverAPIClient(String url, String method, Map<String, String> body, String ...headers) {
        this.url = url;
        this.method = method;
        this.body = body;
        this.headers = new HashMap<>();
        for (int i = 0; i < headers.length; i += 2) {
            this.headers.put(headers[i], headers[i + 1]);
        }
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    public Map<String, String> getBody() {
        return body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
