package codesquad.http.property;

public enum HttpMethod {

    GET("GET"),
    POST("POST"),
    ;

    private final String displayName;

    HttpMethod(String displayName) {
        this.displayName = displayName;
    }

    public static HttpMethod of(String method) {
        method = method.toUpperCase();

        return switch (method) {
            case "GET" -> GET;
            case "POST" -> POST;
            default -> throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        };
    }

    public String getDisplayName() {
        return displayName;
    }
}
