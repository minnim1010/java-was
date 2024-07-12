package codesquad.business.handler;

import codesquad.http.cookie.Cookie;
import codesquad.http.handler.AbstractDynamicRequestHandler;
import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;
import codesquad.http.property.HttpStatus;
import codesquad.http.session.Session;

public class LogoutHandler extends AbstractDynamicRequestHandler {

    @Override
    public void processGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        Session session = httpRequest.getSession();
        if (session == null || session.getAttribute("userId") == null) {
            httpResponse.setStatus(HttpStatus.FOUND);
            httpResponse.setHeader("Location", "/login");
            return;
        }

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

        httpResponse.setStatus(HttpStatus.FOUND);
        httpResponse.setHeader("Location", "/index.html");
    }
}
