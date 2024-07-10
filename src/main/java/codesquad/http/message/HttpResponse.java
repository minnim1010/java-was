package codesquad.http.message;

import static codesquad.http.header.HeaderField.CONTENT_LENGTH;

import codesquad.http.cookie.Cookie;
import codesquad.http.property.HttpStatus;
import codesquad.http.property.HttpVersion;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class HttpResponse {

    private final Map<String, String> headers;
    private HttpVersion version;
    private HttpStatus status;
    private byte[] body;

    public HttpResponse() {
        this.headers = new HashMap<>();
        this.body = new byte[0];
    }

    public HttpResponse(HttpStatus status) {
        this.version = HttpVersion.HTTP_1_1;
        this.headers = new HashMap<>();
        this.body = new byte[0];
        this.status = status;
    }

    public HttpResponse(HttpVersion version, HttpStatus status, Map<String, String> headers, byte[] body) {
        this.version = version;
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public void setHeader(String key, String value) {
        if (headers.containsKey(key)) {
            throw new IllegalStateException(key + " header is already set");
        }
        this.headers.put(key, value);
    }

    public void setBody(byte[] body) {
        if (this.body.length != 0) {
            throw new IllegalStateException("body is already set");
        }
        this.body = body.clone();
        setHeader(CONTENT_LENGTH.getFieldName(), String.valueOf(body.length));
    }

    public byte[] getBody() {
        return body;
    }

    public HttpVersion getVersion() {
        return version;
    }

    public void setVersion(HttpVersion version) {
        if (this.version != null) {
            throw new IllegalStateException("version is already set");
        }
        this.version = version;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        if (this.status != null) {
            throw new IllegalStateException("status is already set");
        }
        this.status = status;
    }

    public void setCookie(Cookie cookie) {
        StringBuilder cookieBuilder = new StringBuilder();
        cookieBuilder.append(cookie.getName()).append("=").append(cookie.getValue());

        if (cookie.getMaxAge() > 0) {
            cookieBuilder.append("; Max-Age=").append(cookie.getMaxAge());
        }
        if (cookie.getDomain() != null) {
            cookieBuilder.append("; Domain=").append(cookie.getDomain());
        }
        if (cookie.getPath() != null) {
            cookieBuilder.append("; Path=").append(cookie.getPath());
        }
        if (cookie.isSecure()) {
            cookieBuilder.append("; Secure");
        }
        if (cookie.isHttpOnly()) {
            cookieBuilder.append("; HttpOnly");
        }

        setHeader("Set-Cookie", cookieBuilder.toString());
    }

    /**
     * Formats the HTTP response into a byte array suitable for network transmission. It should be called only once per
     * response object to prevent duplicate headers or body content.
     *
     * @return
     * @throws IOException
     */
    public byte[] format() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        String statusLine = version.getDisplayName() + " " +
                status.getCode() + " " +
                status.getMessage() + "\r\n";
        outputStream.write(statusLine.getBytes("UTF-8"));

        for (Entry<String, String> entry : headers.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            String header = key + ": " + value + "\r\n";
            outputStream.write(header.getBytes("UTF-8"));
        }
        outputStream.write("\r\n".getBytes("UTF-8"));

        outputStream.write(body);

        return outputStream.toByteArray();
    }

    public Cookie getCookie(String name) {
        String cookie = headers.get("Set-Cookie");
        if (cookie == null) {
            return null;
        }
        Cookie parse = Cookie.parse(cookie);
        if (parse.getName().equals(name)) {
            return parse;
        }
        return null;
    }
}
