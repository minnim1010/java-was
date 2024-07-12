package codesquad.business.handler;

import codesquad.business.model.User;
import codesquad.business.persistence.UserRepository;
import codesquad.http.cookie.Cookie;
import codesquad.http.handler.AbstractDynamicRequestHandler;
import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;
import codesquad.http.property.HttpStatus;
import codesquad.http.session.Session;
import codesquad.http.session.SessionManager;

public class LoginHandler extends AbstractDynamicRequestHandler {

    private final UserRepository userRepository;

    public LoginHandler(UserRepository userRepository) {
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
            httpResponse.setHeader("Location", "/user/login_failed.html");
            return;
        }

        Session session = httpRequest.getSession();
        if (session == null) {
            session = SessionManager.getInstance().createSession();
            session.setAttribute("userId", userId);
        }

        Cookie cookie = new Cookie("SID",
                session.getSessionId(),
                null,
                "/",
                session.remainSeconds(),
                false,
                true);
        httpResponse.setCookie(cookie);

        httpResponse.setStatus(HttpStatus.FOUND);
        httpResponse.setHeader("Location", "/index.html");
    }
}
