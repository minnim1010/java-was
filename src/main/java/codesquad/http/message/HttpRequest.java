package codesquad.http.message;

import static codesquad.utils.StringUtils.BLANK;
import static codesquad.utils.StringUtils.NEW_LINE;

import codesquad.http.parser.MultipartFormDataParser.Part;
import codesquad.http.property.HttpMethod;
import codesquad.http.property.HttpVersion;
import codesquad.http.session.Session;
import java.net.URI;
import java.util.Map;

public class HttpRequest {

    protected final HttpMethod method;
    protected final URI uri;
    protected final Map<String, String> query;
    protected final HttpVersion version;
    protected final Map<String, String> headers;
    protected final byte[] body;
    protected final Map<String, Part> multipartParts;
    protected Session session;

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
        this.multipartParts = Map.of();
    }

    public HttpRequest(HttpMethod method,
                       URI uri,
                       Map<String, String> query,
                       HttpVersion version,
                       Map<String, String> headers,
                       byte[] body,
                       Map<String, Part> multipartParts) {
        this.method = method;
        this.uri = uri;
        this.query = query;
        this.version = version;
        this.headers = headers;
        this.body = body;
        this.multipartParts = multipartParts;
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

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        if (this.session != null) {
            throw new IllegalArgumentException("Session already exists");
        }

        this.session = session;
    }

    public Part getMultipartParts(String name) {
        return multipartParts.get(name);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[Request]").append(NEW_LINE);
        sb.append(method).append(BLANK)
                .append(uri).append(BLANK)
                .append(version.getDisplayName()).append(NEW_LINE);
        headers.forEach((key, value) -> sb.append(key).append(": ").append(value).append(NEW_LINE));
        sb.append("\n");
        sb.append(new String(body));
        return sb.toString();
    }
}
