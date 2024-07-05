package codesquad.http;

import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;

public interface RequestHandler {

    void processGet(HttpRequest httpRequest, HttpResponse httpResponse);

    void processPost(HttpRequest httpRequest, HttpResponse httpResponse);
}
