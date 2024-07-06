package codesquad.http;

import static codesquad.http.header.HeaderField.DATE;

import codesquad.config.GlobalConfig;
import codesquad.error.HttpRequestParseException;
import codesquad.error.ResourceNotFoundException;
import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;
import codesquad.http.parser.HttpParser;
import codesquad.http.property.HttpStatus;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpProcessor {

    private static final Logger log = LoggerFactory.getLogger(HttpProcessor.class);

    private final HttpParser httpParser;
    private final HttpRequestProcessor httpRequestProcessor;

    public HttpProcessor(HttpParser httpParser,
                         HttpRequestProcessor httpRequestProcessor) {
        this.httpParser = httpParser;
        this.httpRequestProcessor = httpRequestProcessor;
    }

    public byte[] process(String input) throws IOException {
        HttpResponse response = new HttpResponse();

        try {
            HttpRequest request = httpParser.parse(input);
            httpRequestProcessor.processRequest(request, response);
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

        setDateHeader(response);

        return response.format();
    }

    private void setDateHeader(HttpResponse httpResponse) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", GlobalConfig.LOCALE);
        dateFormat.setTimeZone(TimeZone.getTimeZone(GlobalConfig.TIMEZONE));
        String date = dateFormat.format(new Date());

        httpResponse.setHeader(DATE.getFieldName(), date);
    }
}
