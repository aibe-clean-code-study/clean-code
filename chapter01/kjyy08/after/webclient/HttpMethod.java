package kjyy08.after.webclient;

public enum HttpMethod {
    GET("GET"), POST("POST");
    final String name;

    HttpMethod(String method) {
        this.name = method;
    }
}