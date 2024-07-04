package codesquad.http;

import codesquad.error.HttpRequestParseException;
import codesquad.error.ResourceNotFoundException;
import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;
import codesquad.http.parser.HttpParser;
import codesquad.http.property.HttpStatus;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpProcessor {

    private static final Logger log = LoggerFactory.getLogger(HttpProcessor.class);

    private static final HttpParser httpParser = new HttpParser();
    private static final HttpRequestProcessor httpRequestProcessor = new HttpRequestProcessor();

    public byte[] process(String input) throws IOException {
        HttpRequest request = null;
        HttpResponse response = new HttpResponse();

        try {
            request = httpParser.parse(input);
            httpRequestProcessor.processRequest(request, response);
            response.setDateHeader();
        } catch (Exception e) {
            if (e instanceof HttpRequestParseException) {
                response = new HttpResponse(HttpStatus.BAD_REQUEST);
            } else if (e instanceof ResourceNotFoundException) {
                response = new HttpResponse(HttpStatus.NOT_FOUND);
            } else if (e instanceof UnsupportedOperationException) {
                response = new HttpResponse(HttpStatus.NOT_ACCEPTABLE);
            } else {
                response = new HttpResponse(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            log.error(e.getMessage(), e);
        }

        return response.format();
    }
}
