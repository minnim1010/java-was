package codesquad.http;

import codesquad.http.property.HttpStatus;
import codesquad.http.property.HttpVersion;
import java.util.Map;

public record MyHttpResponse(HttpVersion version,
                             HttpStatus status,
                             Map<String, String> headers,
                             String body) {

}
