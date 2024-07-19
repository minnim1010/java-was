package codesquad.http;

import static codesquad.http.header.HeaderField.CONTENT_TYPE;

import codesquad.http.error.HttpRequestParseException;
import codesquad.http.error.ResourceNotFoundException;
import codesquad.http.error.UnSupportedHttpMethodException;
import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;
import codesquad.http.property.HttpStatus;
import codesquad.socket.SocketReader;
import codesquad.socket.SocketWriter;
import codesquad.template.TemplateContext;
import codesquad.template.TemplateEngine;
import java.io.IOException;
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

    private static void handleError(HttpRequest request, HttpResponse response, Exception e) {
        TemplateContext templateContext = new TemplateContext();
        if (request != null && request.getSession() != null && request.getSession().getAttribute("userId") != null) {
            templateContext.setValue("user", request.getSession().getAttribute("userId"));
        }
        templateContext.setValue("errorCode", response.getStatus().getCode());
        templateContext.setValue("errorMessage", e.getMessage());

        String renderedTemplate = TemplateEngine.getInstance().render("/error.html", templateContext);

        response.setBody(renderedTemplate.getBytes());
        response.setHeader(CONTENT_TYPE.getFieldName(), "text/html");
    }

    public void process(SocketReader socketReader,
                        SocketWriter socketWriter) throws IOException {
        HttpRequest request = null;
        HttpResponse response = new HttpResponse();

        try {
            request = httpRequestPreprocessor.process(socketReader);
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

            handleError(request, response, e);
            log.error(e.getMessage(), e);
        }

        socketWriter.write(response.format());
    }
}
