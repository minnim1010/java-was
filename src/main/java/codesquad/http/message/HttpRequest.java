package codesquad.http.message;

import static codesquad.http.HttpConstraints.HEADER_DELIMITER;
import static codesquad.utils.StringUtils.BLANK;
import static codesquad.utils.StringUtils.NEW_LINE;

import codesquad.http.property.HttpMethod;
import codesquad.http.property.HttpVersion;
import java.util.Map;

public record HttpRequest(HttpMethod method,
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
        sb.append("[Request]").append(NEW_LINE);
        sb.append(method).append(BLANK)
                .append(path).append(BLANK)
                .append(version.getVersion()).append(NEW_LINE);
        headers.forEach((key, value) -> sb.append(key).append(HEADER_DELIMITER).append(value).append(NEW_LINE));
        sb.append("\n");
        sb.append(body);
        return sb.toString();
    }
}
