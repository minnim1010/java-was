package codesquad.parser;

import java.util.Map;

public record MyHttpRequest(String method,
                            String path,
                            Map<String, String> headers,
                            String body) {
}
