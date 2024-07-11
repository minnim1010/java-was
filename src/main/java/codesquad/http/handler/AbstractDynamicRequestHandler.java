package codesquad.http.handler;

import codesquad.http.error.UnSupportedHttpMethodException;
import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;

public abstract class AbstractDynamicRequestHandler implements DynamicRequestHandler {

    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse) {
        switch (httpRequest.getMethod()) {
            case GET -> processGet(httpRequest, httpResponse);
            case POST -> processPost(httpRequest, httpResponse);
            default -> throw new UnsupportedOperationException("Unsupported HTTP Method " + httpRequest.getMethod());
        }
    }

    @Override
    public void processGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        throw new UnSupportedHttpMethodException("Unsupported GET method");
    }

    @Override
    public void processPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        throw new UnSupportedHttpMethodException("Unsupported POST method");
    }
}
