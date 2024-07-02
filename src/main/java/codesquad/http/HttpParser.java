package codesquad.http;

import static codesquad.http.HttpConstraints.CRLF;

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

        String[] requestLine = lines[0].split(" ");
        String method = parseMethod(requestLine);
        String path = requestLine[1];
        HttpVersion version = HttpVersion.of(requestLine[2]);

        Map<String, String> headers = new HashMap<>();
        int curLineIdx = 1;
        for (; curLineIdx < lines.length; curLineIdx++) {
            String line = lines[curLineIdx];
            if (line.isEmpty()) {
                break;
            }
            if (!line.contains(": ")) {
                break;
            }
            String[] header = line.split(": ");
            headers.put(header[0], header[1]);
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

    private String parseMethod(String[] requestLine) {
        String method = requestLine[0];

        return switch (method.toUpperCase()) {
            case "GET" -> "GET";
            case "POST" -> "POST";
            default -> throw new UnsupportedOperationException("Unsupported HTTP method: " + method);
        };
    }
}
