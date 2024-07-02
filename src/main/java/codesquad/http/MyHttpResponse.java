package codesquad.http;

import java.util.Map;

public record MyHttpResponse(HttpVersion version,
                             HttpStatus status,
                             Map<String, String> headers,
                             String body) {

}
