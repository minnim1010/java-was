package codesquad.http.property;

public enum HttpMethod {

    GET("GET"),
    POST("POST"),
    ;

    private final String displayName;

    HttpMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
