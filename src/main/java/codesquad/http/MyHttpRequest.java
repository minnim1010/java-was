package codesquad.http;

import codesquad.http.property.HttpVersion;
import java.util.Map;

public record MyHttpRequest(String method,
                            String path,
                            HttpVersion version,
                            Map<String, String> headers,
                            String body) {

    public String getHeader(String key) {
        return headers.get(key);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[Request]\n");
        sb.append(method).append(" ").append(path).append(" ").append(version.getVersion()).append("\n");
        headers.forEach((key, value) -> sb.append(key).append(": ").append(value).append("\n"));
        sb.append("\n");
        sb.append(body);
        return sb.toString();
    }
}
