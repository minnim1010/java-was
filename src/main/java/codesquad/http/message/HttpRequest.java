package codesquad.http.message;

import static codesquad.http.HttpConstraints.HEADER_DELIMITER;
import static codesquad.utils.StringUtils.BLANK;
import static codesquad.utils.StringUtils.NEW_LINE;

import codesquad.http.property.HttpMethod;
import codesquad.http.property.HttpVersion;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private final HttpMethod method;
    private final URI uri;
    private final Map<String, String> query;
    private final HttpVersion version;
    private final Map<String, String> headers;
    private final byte[] body;

    public HttpRequest(HttpMethod method,
                       URI uri,
                       Map<String, String> query,
                       HttpVersion version,
                       Map<String, String> headers,
                       byte[] body) {
        this.method = method;
        this.uri = uri;
        this.query = query;
        this.version = version;
        this.headers = headers;
        this.body = body;
    }

    public HttpRequest(HttpMethod method,
                       URI uri,
                       HttpVersion version,
                       Map<String, String> headers,
                       byte[] body) {
        this(method, uri, new HashMap<>(), version, headers, body);
    }

    public HttpVersion getVersion() {
        return version;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public URI getUri() {
        return uri;
    }

    public byte[] getBody() {
        return body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getHeader(String key) {
        return headers.getOrDefault(key, null);
    }

    public String getQuery(String key) {
        return query.get(key);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[Request]").append(NEW_LINE);
        sb.append(method).append(BLANK)
                .append(uri).append(BLANK)
                .append(version.getDisplayName()).append(NEW_LINE);
        headers.forEach((key, value) -> sb.append(key).append(HEADER_DELIMITER).append(value).append(NEW_LINE));
        sb.append("\n");
        sb.append(new String(body));
        return sb.toString();
    }
}
