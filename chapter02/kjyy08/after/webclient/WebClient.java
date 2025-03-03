package kjyy08.after.webclient;

import java.net.http.HttpRequest;

interface WebClient {
    String sendRequest(HttpRequest request);
    HttpRequest makeRequest(String path, HttpMethod method, String body, String... headers);
}