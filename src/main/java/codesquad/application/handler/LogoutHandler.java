package codesquad.application.handler;

import codesquad.http.cookie.Cookie;
import codesquad.http.handler.AbstractDynamicRequestHandler;
import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;
import codesquad.http.session.Session;

public class LogoutHandler extends AbstractDynamicRequestHandler {

    @Override
    public void processGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        Session session = httpRequest.getSession();
        String sessionId = session.getSessionId();
        session.invalidate();

        Cookie cookie = new Cookie("SID",
                sessionId,
                null,
                "/",
                -1,
                false,
                true);
        httpResponse.setCookie(cookie);

        httpResponse.sendRedirect("/index.html");
    }
}
