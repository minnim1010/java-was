package codesquad.http;

import static codesquad.http.HttpConstraints.CRLF;
import static codesquad.http.HttpConstraints.HEADER_DELIMITER;
import static codesquad.utils.StringUtils.BLANK;

import codesquad.error.HttpRequestParseException;
import codesquad.http.message.HttpRequest;
import codesquad.http.property.HttpMethod;
import codesquad.http.property.HttpVersion;
import java.net.URI;
import java.net.URISyntaxException;
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

            String[] requestLine = lines[0].split(BLANK);
            HttpMethod method = HttpMethod.of(requestLine[0]);
            URI uri = null;
            try {
                uri = new URI(requestLine[1]);
            } catch (URISyntaxException e) {
                throw new HttpRequestParseException("Invalid request", e);
            }
            String query = uri.getQuery();
            Map<String, String> queryMap = queryParser.parseQuery(query);

            HttpVersion version = HttpVersion.of(requestLine[2]);

            Map<String, String> headers = new HashMap<>();
            int curLineIdx = 1;
            while (curLineIdx < lines.length && lines[curLineIdx].contains(HEADER_DELIMITER)) {
                String[] header = lines[curLineIdx++].split(HEADER_DELIMITER);
                String headerType = header[0];
                String headerValue = header[1];

                //todo: refactor 변수명 위에 정의한 대로 수정, 분기 처리 대신 map api 사용해서 리팩토링할 있나 확인
                if (headers.containsKey(header[0])) {
                    headers.put(headerType, headers.get(headerType) + ", " + headerValue);
                } else {
                    headers.put(header[0], header[1]);
                }
            }

            StringBuilder body = new StringBuilder();

            for (curLineIdx = curLineIdx + 1; curLineIdx < lines.length; curLineIdx++) {
                body.append(lines[curLineIdx]);
            }

            HttpRequest httpRequest = new HttpRequest(method, uri, queryMap, version, headers, body.toString());
            log.debug(httpRequest.toString());
            return httpRequest;
        } catch (Exception e) {
            throw new HttpRequestParseException("Invalid request");
        }
    }
}
