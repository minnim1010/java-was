package codesquad.business.handler;

import codesquad.http.cookie.Cookie;
import codesquad.http.handler.AbstractRequestHandler;
import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;
import codesquad.http.property.HttpStatus;
import codesquad.http.session.Session;

public class LogoutRequestHandler extends AbstractRequestHandler {

    @Override
    public void processGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        Session session = httpRequest.getSession();
        if (session == null) {
            httpResponse.setStatus(HttpStatus.UNAUTHORIZED);
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
