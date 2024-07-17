package codesquad.http.parser;

import static codesquad.utils.StringUtils.BLANK;

import codesquad.http.error.HttpRequestParseException;
import codesquad.http.message.HttpRequest;
import codesquad.http.parser.MultipartFormDataParser.Part;
import codesquad.http.property.HttpMethod;
import codesquad.http.property.HttpVersion;
import codesquad.socket.SocketReader;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpParser {

    private static final Logger log = LoggerFactory.getLogger(HttpParser.class);

    private HttpParser() {
    }

    public static HttpRequest parse(SocketReader socketReader) throws HttpRequestParseException {
        try {
            String requestLine = new String(socketReader.readLine()).trim();

            // start line
            String[] startLineTokens = requestLine.split(BLANK);
            if (startLineTokens.length != 3) {
                throw new HttpRequestParseException("Invalid start line: " + requestLine);
            }

            HttpMethod method = HttpMethod.valueOf(startLineTokens[0]);
            URI uri = URI.create(startLineTokens[1]);
            HttpVersion version = HttpVersion.of(startLineTokens[2]);

            // headers
            Map<String, String> headers = parseHeaders(socketReader);

            // body
            byte[] body = new byte[0];
            Map<String, Part> multipartFormDatas = new HashMap<>();
            if (headers.containsKey("Content-Type") && headers.get("Content-Type").startsWith("multipart/form-data")) {
                multipartFormDatas.putAll(parseMultipartFormData(
                        socketReader.readBytes(Integer.parseInt(headers.get("Content-Length"))), headers));
            } else {
                body = parseBody(socketReader, headers);
            }

            // query
            Map<String, String> queryMap = parseQuery(uri, method, headers, body);

            HttpRequest httpRequest = new HttpRequest(method, uri, queryMap, version, headers, body,
                    multipartFormDatas);
            log.debug(httpRequest.toString());
            log.info(method + " " + uri + " ");

            return httpRequest;
        } catch (Exception e) {
            throw new HttpRequestParseException("Failed to parse HTTP request", e);
        }
    }

    private static Map<String, String> parseHeaders(SocketReader socketReader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while (!(line = new String(socketReader.readLine()).trim()).isEmpty()) {
            String[] headerTokens = line.split(":", 2);
            if (headerTokens.length != 2) {
                throw new HttpRequestParseException("Invalid header: " + line);
            }

            headers.merge(headerTokens[0].trim(), headerTokens[1].trim(),
                    (existVal, newVal) -> existVal + ", " + newVal);
        }
        return headers;
    }

    private static byte[] parseBody(SocketReader socketReader,
                                    Map<String, String> headers) throws IOException {
        if (!headers.containsKey("Content-Length")) {
            return new byte[0];
        }

        int contentLength = Integer.parseInt(headers.get("Content-Length"));
        String body = new String(socketReader.readBytes(contentLength));
        return body.getBytes();
    }

    private static Map<String, Part> parseMultipartFormData(byte[] body, Map<String, String> headers) {
        String boundary = headers.get("Content-Type").split("boundary=")[1];
        return MultipartFormDataParser.parse(body, boundary);
    }

    private static Map<String, String> parseQuery(URI uri,
                                                  HttpMethod method,
                                                  Map<String, String> headers,
                                                  byte[] body) {
        Map<String, String> queryMap = QueryParser.parse(uri.getQuery());

        if (method == HttpMethod.POST && headers.containsKey("Content-Type") &&
                headers.get("Content-Type").equals("application/x-www-form-urlencoded")) {
            queryMap.putAll(QueryParser.parse(new String(body)));
        }
        return queryMap;
    }
}
