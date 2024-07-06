package codesquad.business.processor;

import codesquad.business.model.User;
import codesquad.http.RequestHandler;
import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;
import codesquad.http.property.HttpStatus;

public class UserProcessor implements RequestHandler {

    @Override
    public void processGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        String userId = httpRequest.getQuery("userId");
        String name = httpRequest.getQuery("name");
        String email = httpRequest.getQuery("email");
        String password = httpRequest.getQuery("password");

        User user = new User(userId, password, name, email);

        httpResponse.setStatus(HttpStatus.MOVED_PERMANENTLY);
        httpResponse.setHeader("Location", "/index.html");
    }

    @Override
    public void processPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        // do nothing
    }
}
