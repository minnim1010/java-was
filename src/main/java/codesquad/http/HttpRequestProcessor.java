package codesquad.http;

import static codesquad.http.header.HeaderField.DATE;

import codesquad.config.GlobalConstants;
import codesquad.http.handler.DynamicRequestHandler;
import codesquad.http.handler.DynamicRequestHandlerResolver;
import codesquad.http.handler.RequestHandler;
import codesquad.http.handler.StaticResourceRequestHandler;
import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;
import codesquad.http.session.Session;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;

public class HttpRequestProcessor {

    private static final SimpleDateFormat dateFormat;

    static {
        GlobalConstants globalConstants = GlobalConstants.getInstance();

        dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", globalConstants.getLocale());
        dateFormat.setTimeZone(TimeZone.getTimeZone(globalConstants.getTimezone()));
    }

    private final DynamicRequestHandlerResolver dynamicRequestHandlerResolver;
    private final StaticResourceRequestHandler staticResourceRequestHandler;
    private final Set<String> authenticatedURI = Set.of("/write.html", "/user/list", "/logout", "/article/write");

    public HttpRequestProcessor(DynamicRequestHandlerResolver dynamicRequestHandlerResolver,
                                StaticResourceRequestHandler staticResourceRequestHandler) {
        this.dynamicRequestHandlerResolver = dynamicRequestHandlerResolver;
        this.staticResourceRequestHandler = staticResourceRequestHandler;
    }

    public void processRequest(HttpRequest httpRequest,
                               HttpResponse httpResponse) throws IOException {
        httpResponse.setVersion(httpRequest.getVersion());

        RequestHandler requestHandler = resolveRequestHandler(httpRequest.getUri().getPath());

        URI uri = httpRequest.getUri();
        if (authenticatedURI.contains(uri.getPath())) {
            Session session = httpRequest.getSession();
            if (session == null || session.getAttribute("userId") == null) {
                httpResponse.sendRedirect("/login");
                return;
            }
        }

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
        String date = dateFormat.format(new Date());
        httpResponse.setHeader(DATE.getFieldName(), date);
    }
}