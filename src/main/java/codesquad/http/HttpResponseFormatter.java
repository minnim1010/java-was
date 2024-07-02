package codesquad.http;

import java.util.Map;

public class HttpResponseFormatter {

    public String formatResponse(MyHttpResponse httpResponse) {
        return String.format("%s %d %s\r\n%s\r\n%s",
                httpResponse.version().getVersion(),
                httpResponse.status().getStatusCode(),
                httpResponse.status().getStatusMessage(),
                formatHeaders(httpResponse.headers()),
                httpResponse.body());
    }

    private String formatHeaders(Map<String, String> headers) {
        assert headers != null;

        StringBuilder headerString = new StringBuilder();
        headers.forEach((key, value) -> headerString
                .append(key)
                .append(": ")
                .append(value)
                .append("\r\n"));
        return headerString.toString();
    }
}
