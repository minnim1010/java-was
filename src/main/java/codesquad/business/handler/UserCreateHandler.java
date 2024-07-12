package codesquad.business.handler;

import codesquad.business.model.User;
import codesquad.business.persistence.UserRepository;
import codesquad.http.handler.AbstractDynamicRequestHandler;
import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;
import codesquad.http.property.HttpStatus;

public class UserCreateHandler extends AbstractDynamicRequestHandler {

    private final UserRepository userRepository;

    public UserCreateHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void processPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        String userId = httpRequest.getQuery("userId");
        String name = httpRequest.getQuery("name");
        String email = httpRequest.getQuery("email");
        String password = httpRequest.getQuery("password");

        User user = new User(userId, password, name, email);
        userRepository.findById(userId)
                .ifPresentOrElse(u -> {
                    httpResponse.setStatus(HttpStatus.FOUND);
                    httpResponse.setHeader("Location", "/user/regist_failed.html");
                }, () -> {
                    userRepository.save(user);

                    httpResponse.setStatus(HttpStatus.FOUND);
                    httpResponse.setHeader("Location", "/index.html");
                });
    }
}
