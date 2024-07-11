package codesquad.http;

import static codesquad.http.header.HeaderField.DATE;

import codesquad.config.GlobalConstants;
import codesquad.http.error.HttpRequestParseException;
import codesquad.http.error.ResourceNotFoundException;
import codesquad.http.error.UnSupportedHttpMethodException;
import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;
import codesquad.http.property.HttpStatus;
import codesquad.socket.Reader;
import codesquad.socket.Writer;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpProcessor {

    private static final Logger log = LoggerFactory.getLogger(HttpProcessor.class);

    private final HttpRequestPreprocessor httpRequestPreprocessor;
    private final HttpRequestProcessor httpRequestProcessor;

    public HttpProcessor(HttpRequestPreprocessor httpRequestPreprocessor,
                         HttpRequestProcessor httpRequestProcessor) {
        this.httpRequestPreprocessor = httpRequestPreprocessor;
        this.httpRequestProcessor = httpRequestProcessor;
    }

    public void process(Reader reader,
                        Writer writer) throws IOException {
        HttpResponse response = new HttpResponse();

        try {
            HttpRequest request = httpRequestPreprocessor.process(reader);
            httpRequestProcessor.processRequest(request, response);
        } catch (Exception e) {
            if (e instanceof HttpRequestParseException) {
                response = new HttpResponse(HttpStatus.BAD_REQUEST);
            } else if (e instanceof ResourceNotFoundException) {
                response = new HttpResponse(HttpStatus.NOT_FOUND);
            } else if (e instanceof UnsupportedOperationException) {
                response = new HttpResponse(HttpStatus.NOT_ACCEPTABLE);
            } else if (e instanceof UnSupportedHttpMethodException) {
                response = new HttpResponse(HttpStatus.METHOD_NOT_ALLOWED);
            } else {
                response = new HttpResponse(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            log.error(e.getMessage(), e);
        }

        setDateHeader(response);

        writer.write(response.format());
    }

    private void setDateHeader(HttpResponse httpResponse) {
        Locale locale = GlobalConstants.getInstance().getLocale();
        String timezone = GlobalConstants.getInstance().getTimezone();

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", locale);
        dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
        String date = dateFormat.format(new Date());

        httpResponse.setHeader(DATE.getFieldName(), date);
    }
}
