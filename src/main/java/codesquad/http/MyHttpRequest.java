package codesquad.http;

import java.util.Map;

public record MyHttpRequest(String method,
                            String path,
                            Map<String, String> headers,
                            String body) {

    public String getHeader(String key) {
        return headers.get(key);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[Request]\n");
        sb.append(method).append(" ").append(path).append("\n");
        headers.forEach((key, value) -> sb.append(key).append(": ").append(value).append("\n"));
        sb.append("\n");
        sb.append(body);
        return sb.toString();
    }
}
