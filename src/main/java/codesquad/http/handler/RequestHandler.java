package codesquad.http.handler;

import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;
import java.io.IOException;

public interface RequestHandler {

    void handle(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException;
}
