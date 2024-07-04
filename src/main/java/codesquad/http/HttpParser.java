package codesquad.http;

import static codesquad.http.HttpConstraints.CRLF;
import static codesquad.http.HttpConstraints.HEADER_DELIMITER;
import static codesquad.utils.StringUtils.BLANK;

import codesquad.error.HttpRequestParseException;
import codesquad.http.message.HttpRequest;
import codesquad.http.property.HttpMethod;
import codesquad.http.property.HttpVersion;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpParser {

    private static final Logger log = LoggerFactory.getLogger(HttpParser.class);
    private static final QueryParser queryParser = new QueryParser();

    public HttpRequest parse(String httpRequestStr) throws HttpRequestParseException {
        try {
            String[] lines = httpRequestStr.split(CRLF);

            // start line
            String[] requestLine = lines[0].split(BLANK);
            HttpMethod method = HttpMethod.of(requestLine[0]);
            URI uri = new URI(requestLine[1]);
            String query = uri.getQuery();
            Map<String, String> queryMap = queryParser.parseQuery(query);
            HttpVersion version = HttpVersion.of(requestLine[2]);

            // headers
            Map<String, String> headers = new HashMap<>();
            int lineIndex = 1;
            while (lineIndex < lines.length && lines[lineIndex].contains(HEADER_DELIMITER)) {
                String[] headerParts = lines[lineIndex++].split(HEADER_DELIMITER, 2);
                String headerKey = headerParts[0].trim();
                String headerValue = headerParts[1].trim();

                headers.merge(headerKey, headerValue, (existingValue, newValue) -> existingValue + ", " + newValue);
            }

            // body
            StringBuilder body = new StringBuilder();
            for (lineIndex = lineIndex + 1; lineIndex < lines.length; lineIndex++) {
                body.append(lines[lineIndex]);
            }

            HttpRequest httpRequest = new HttpRequest(method, uri, queryMap, version, headers, body.toString());
            log.debug(httpRequest.toString());

            return httpRequest;
        } catch (Exception e) {
            throw new HttpRequestParseException("Failed to parse HTTP request", e);
        }
    }
}
