package codesquad.http;

import codesquad.http.handler.RequestHandler;
import codesquad.http.handler.RequestHandlerResolver;
import codesquad.http.handler.StaticResourceRequestHandler;
import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;
import codesquad.http.property.HttpMethod;
import java.io.IOException;

public class HttpRequestProcessor {

    private final RequestHandlerResolver requestHandlerResolver;
    private final StaticResourceRequestHandler staticResourceRequestHandler;

    public HttpRequestProcessor(RequestHandlerResolver requestHandlerResolver,
                                StaticResourceRequestHandler staticResourceRequestHandler) {
        this.requestHandlerResolver = requestHandlerResolver;
        this.staticResourceRequestHandler = staticResourceRequestHandler;
    }

    public void processRequest(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        HttpMethod method = httpRequest.getMethod();
        String path = httpRequest.getUri().getPath();

        httpResponse.setVersion(httpRequest.getVersion());

        RequestHandler requestHandler = requestHandlerResolver.resolve(path);
        if (requestHandler == null) {
            staticResourceRequestHandler.handle(httpRequest, httpResponse);
            return;
        }

        switch (method) {
            case GET -> requestHandler.processGet(httpRequest, httpResponse);
            case POST -> requestHandler.processPost(httpRequest, httpResponse);
            default -> throw new UnsupportedOperationException("Unsupported HTTP Method " + method);
        }
    }
}