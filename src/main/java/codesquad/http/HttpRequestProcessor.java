package codesquad.http;

import static codesquad.http.header.HeaderField.DATE;

import codesquad.config.GlobalConstants;
import codesquad.http.handler.DynamicRequestHandler;
import codesquad.http.handler.DynamicRequestHandlerResolver;
import codesquad.http.handler.RequestHandler;
import codesquad.http.handler.StaticResourceRequestHandler;
import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class HttpRequestProcessor {

    private final DynamicRequestHandlerResolver dynamicRequestHandlerResolver;
    private final StaticResourceRequestHandler staticResourceRequestHandler;

    public HttpRequestProcessor(DynamicRequestHandlerResolver dynamicRequestHandlerResolver,
                                StaticResourceRequestHandler staticResourceRequestHandler) {
        this.dynamicRequestHandlerResolver = dynamicRequestHandlerResolver;
        this.staticResourceRequestHandler = staticResourceRequestHandler;
    }

    public void processRequest(HttpRequest httpRequest,
                               HttpResponse httpResponse) throws IOException {
        httpResponse.setVersion(httpRequest.getVersion());

        RequestHandler requestHandler = resolveRequestHandler(httpRequest.getUri().getPath());
        requestHandler.handle(httpRequest, httpResponse);

        setDateHeader(httpResponse);
    }

    private RequestHandler resolveRequestHandler(String path) {
        DynamicRequestHandler dynamicRequestHandler = dynamicRequestHandlerResolver.resolve(path);
        if (dynamicRequestHandler != null) {
            return dynamicRequestHandler;
        }

        return staticResourceRequestHandler;
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