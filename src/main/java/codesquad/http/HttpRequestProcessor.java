package codesquad.http;

import codesquad.http.handler.DynamicRequestHandler;
import codesquad.http.handler.DynamicRequestHandlerResolver;
import codesquad.http.handler.StaticResourceRequestHandler;
import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;
import codesquad.http.property.HttpMethod;
import java.io.IOException;

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
        HttpMethod method = httpRequest.getMethod();
        String path = httpRequest.getUri().getPath();

        httpResponse.setVersion(httpRequest.getVersion());

        DynamicRequestHandler dynamicRequestHandler = dynamicRequestHandlerResolver.resolve(path);
        if (dynamicRequestHandler == null) {
            staticResourceRequestHandler.handle(httpRequest, httpResponse);
            return;
        }

        switch (method) {
            case GET -> dynamicRequestHandler.processGet(httpRequest, httpResponse);
            case POST -> dynamicRequestHandler.processPost(httpRequest, httpResponse);
            default -> throw new UnsupportedOperationException("Unsupported HTTP Method " + method);
        }
    }
}