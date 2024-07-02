package codesquad.http.property;

public enum HttpVersion {
    HTTP_1_0("HTTP/1.0"),
    HTTP_1_1("HTTP/1.1");

    private final String version;

    HttpVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public static HttpVersion of(String version) {
        return switch (version) {
            case "HTTP/1.0" -> HTTP_1_0;
            case "HTTP/1.1" -> HTTP_1_1;
            default -> throw new IllegalArgumentException("Unsupported HTTP version: " + version);
        };
    }
}
