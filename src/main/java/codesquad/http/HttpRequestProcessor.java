package codesquad.http;

import codesquad.http.handler.DynamicRequestHandler;
import codesquad.http.handler.DynamicRequestHandlerResolver;
import codesquad.http.handler.RequestHandler;
import codesquad.http.handler.StaticResourceRequestHandler;
import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;
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
        httpResponse.setVersion(httpRequest.getVersion());

        RequestHandler requestHandler = resolveRequestHandler(httpRequest.getUri().getPath());
        requestHandler.handle(httpRequest, httpResponse);
    }

    private RequestHandler resolveRequestHandler(String path) {
        DynamicRequestHandler dynamicRequestHandler = dynamicRequestHandlerResolver.resolve(path);
        if (dynamicRequestHandler != null) {
            return dynamicRequestHandler;
        }

        return staticResourceRequestHandler;
    }
}