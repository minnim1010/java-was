package codesquad.http;

import static codesquad.http.HttpConstraints.CRLF;
import static codesquad.http.HttpConstraints.HEADER_DELIMITER;
import static codesquad.utils.StringUtils.BLANK;

import codesquad.http.property.HttpVersion;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpParser {

    private static final Logger log = LoggerFactory.getLogger(HttpParser.class);

    public Optional<MyHttpRequest> parse(String httpRequestStr) {
        String[] lines = httpRequestStr.split(CRLF);

        String[] requestLine = lines[0].split(BLANK);
        String method = parseMethod(requestLine[0]);
        String path = requestLine[1];
        HttpVersion version = HttpVersion.of(requestLine[2]);

        Map<String, String> headers = new HashMap<>();
        int curLineIdx = 1;
        while (curLineIdx < lines.length && lines[curLineIdx].contains(HEADER_DELIMITER)) {
            String[] header = lines[curLineIdx++].split(HEADER_DELIMITER);
            String headerType = header[0];
            String headerValue = header[1];

            if (headers.containsKey(header[0])) {
                headers.put(headerType, headers.get(headerType) + ", " + headerValue);
            } else {
                headers.put(header[0], header[1]);
            }
        }

        StringBuilder body = new StringBuilder();
        if (method.equalsIgnoreCase("POST")) {
            for (curLineIdx = curLineIdx + 1; curLineIdx < lines.length; curLineIdx++) {
                body.append(lines[curLineIdx]);
            }
        }

        MyHttpRequest httpRequest = new MyHttpRequest(method, path, version, headers, body.toString());
        log.debug(httpRequest.toString());

        return Optional.of(httpRequest);
    }

    private String parseMethod(String method) {
        return switch (method.toUpperCase()) {
            // todo method enum으로 정의하기
            case "GET" -> "GET";
            case "POST" -> "POST";
            default -> throw new UnsupportedOperationException("Unsupported HTTP method: " + method);
        };
    }
}
