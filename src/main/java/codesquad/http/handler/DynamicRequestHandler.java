package codesquad.http.handler;

import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;

public interface DynamicRequestHandler extends RequestHandler {

    void processGet(HttpRequest httpRequest, HttpResponse httpResponse);

    void processPost(HttpRequest httpRequest, HttpResponse httpResponse);
}
