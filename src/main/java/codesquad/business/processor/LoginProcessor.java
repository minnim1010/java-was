package codesquad.business.processor;

import codesquad.business.model.User;
import codesquad.business.persistence.UserRepository;
import codesquad.http.handler.AbstractRequestHandler;
import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;
import codesquad.http.property.HttpStatus;

public class LoginProcessor extends AbstractRequestHandler {

    private final UserRepository userRepository;

    public LoginProcessor(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void processPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        String userId = httpRequest.getQuery("userId");
        String password = httpRequest.getQuery("password");

        User user = userRepository.findById(userId)
                .orElse(null);

        if (user == null || !user.matchPassword(password)) {
            httpResponse.setStatus(HttpStatus.FOUND);
            httpResponse.setHeader("Location", "/login_failed.html");
            return;
        }

        // todo : 로그인 처리
    }
}
