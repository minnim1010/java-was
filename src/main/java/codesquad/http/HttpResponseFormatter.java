package codesquad.http;

import codesquad.http.property.HttpStatus;
import codesquad.http.property.HttpVersion;
import java.util.HashMap;
import java.util.Map;

public class HttpResponseFormatter {

    public String formatResponse(HttpResponse httpResponse) {
        return String.format("%s %d %s\r%n%s\r%n%s",
                httpResponse.version().getVersion(),
                httpResponse.status().getCode(),
                httpResponse.status().getMessage(),
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

    public String createServerErrorResponse() {
        return formatResponse(new HttpResponse(
                HttpVersion.HTTP_1_1,
                HttpStatus.INTERNAL_SERVER_ERROR,
                new HashMap<>(),
                ""));
    }

    public String createBadRequestResponse() {
        return formatResponse(new HttpResponse(
                HttpVersion.HTTP_1_1,
                HttpStatus.BAD_REQUEST,
                new HashMap<>(),
                ""));
    }

    public String createNotFoundResponse() {
        return formatResponse(new HttpResponse(
                HttpVersion.HTTP_1_1,
                HttpStatus.NOT_FOUND,
                new HashMap<>(),
                ""));
    }
}
