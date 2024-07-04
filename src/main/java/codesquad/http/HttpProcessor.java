package codesquad.http;

import codesquad.error.HttpRequestParseException;
import codesquad.error.ResourceNotFoundException;
import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpProcessor {

    private static final Logger log = LoggerFactory.getLogger(HttpProcessor.class);

    private final HttpParser httpParser;
    private final HttpRequestProcessor httpRequestProcessor;
    private final HttpErrorResponseBuilder httpErrorResponseBuilder;

    public HttpProcessor(HttpParser httpParser,
                         HttpRequestProcessor httpRequestProcessor,
                         HttpErrorResponseBuilder httpErrorResponseBuilder) {
        this.httpParser = httpParser;
        this.httpRequestProcessor = httpRequestProcessor;
        this.httpErrorResponseBuilder = httpErrorResponseBuilder;
    }

    public byte[] process(String input) throws IOException {
        HttpRequest request = null;
        HttpResponse response = new HttpResponse();

        try {
            request = httpParser.parse(input);
            httpRequestProcessor.processRequest(request, response);
            response.setDateHeader();
        } catch (Exception e) {
            if (e instanceof HttpRequestParseException) {
                response = httpErrorResponseBuilder.createBadRequestResponse(request, response);
            } else if (e instanceof ResourceNotFoundException) {
                response = httpErrorResponseBuilder.createNotFoundResponse(request, response);
            } else {
                response = httpErrorResponseBuilder.createServerErrorResponse(request, response);
            }
            log.error(e.getMessage(), e);
        }

        return response.format();
    }
}
