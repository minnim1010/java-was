package codesquad.http.parser;

import static codesquad.http.HttpConstraints.HEADER_DELIMITER;
import static codesquad.utils.StringUtils.BLANK;

import codesquad.error.HttpRequestParseException;
import codesquad.http.message.HttpRequest;
import codesquad.http.property.HttpMethod;
import codesquad.http.property.HttpVersion;
import codesquad.socket.Reader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpParser {

    private static final Logger log = LoggerFactory.getLogger(HttpParser.class);
    private static final QueryParser queryParser = new QueryParser();

    public HttpRequest parse(Reader reader) throws HttpRequestParseException {
        try {
            String requestLine = new String(reader.readLine()).trim();

            // start line
            String[] startLineTokens = requestLine.split(BLANK);
            if (startLineTokens.length != 3) {
                throw new HttpRequestParseException("Invalid start line: " + requestLine);
            }

            HttpMethod method = HttpMethod.valueOf(startLineTokens[0]);
            URI uri = URI.create(startLineTokens[1]);
            HttpVersion version = HttpVersion.of(startLineTokens[2]);

            // headers
            Map<String, String> headers = parseHeaders(reader);

            // body
            byte[] body = parseBody(reader, headers);

            // query
            Map<String, String> queryMap = parseQuery(uri, method, headers, body);

            HttpRequest httpRequest = new HttpRequest(method, uri, queryMap, version, headers, body);
            log.debug(httpRequest.toString());

            return httpRequest;
        } catch (Exception e) {
            throw new HttpRequestParseException("Failed to parse HTTP request", e);
        }
    }

    private Map<String, String> parseHeaders(Reader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while (!(line = new String(reader.readLine()).trim()).isEmpty()) {
            String[] headerTokens = line.split(HEADER_DELIMITER, 2);
            if (headerTokens.length != 2) {
                throw new HttpRequestParseException("Invalid header: " + line);
            }

            headers.merge(headerTokens[0].trim(), headerTokens[1].trim(),
                    (existVal, newVal) -> existVal + ", " + newVal);
        }
        return headers;
    }

    private byte[] parseBody(Reader reader, Map<String, String> headers) throws IOException {
        if (!headers.containsKey("Content-Length")) {
            return new byte[0];
        }
        int contentLength = Integer.parseInt(headers.get("Content-Length"));
        return new String(reader.readBytes(contentLength)).getBytes();
    }

    private Map<String, String> parseQuery(URI uri, HttpMethod method, Map<String, String> headers,
                                           byte[] body) throws UnsupportedEncodingException {
        Map<String, String> queryMap = queryParser.parse(uri.getQuery());

        if (method == HttpMethod.POST && headers.get("Content-Type").equals("application/x-www-form-urlencoded")) {
            String decodedQuery = URLDecoder.decode(new String(body), "UTF-8");
            queryMap.putAll(queryParser.parse(decodedQuery));
        }
        return queryMap;
    }
}
