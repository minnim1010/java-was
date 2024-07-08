package codesquad.http.handler;

import codesquad.error.UnSupportedHttpMethodException;
import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;

public abstract class AbstractRequestHandler implements RequestHandler {

    @Override
    public void processGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        throw new UnSupportedHttpMethodException("Unsupported GET method");
    }

    @Override
    public void processPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        throw new UnSupportedHttpMethodException("Unsupported POST method");
    }
}
