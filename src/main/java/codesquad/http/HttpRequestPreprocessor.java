package codesquad.http;

import static codesquad.http.header.HeaderField.COOKIE;

import codesquad.http.message.HttpRequest;
import codesquad.http.parser.HttpParser;
import codesquad.http.session.Session;
import codesquad.http.session.SessionManager;
import codesquad.socket.Reader;
import java.net.HttpCookie;
import java.util.List;

public class HttpRequestPreprocessor {

    private final SessionManager sessionManager;

    public HttpRequestPreprocessor(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public HttpRequest process(Reader reader) {
        HttpRequest request = HttpParser.parse(reader);
        setSession(request);

        return request;
    }

    private void setSession(HttpRequest request) {
        String cookieValue = request.getHeader(COOKIE.getFieldName());
        if (cookieValue == null) {
            return;
        }

        List<HttpCookie> httpCookies = HttpCookie.parse(cookieValue);

        for (HttpCookie httpCookie : httpCookies) {
            if ("SID".equals(httpCookie.getName())) {
                Session session = sessionManager.getSession(httpCookie.getValue());
                request.setSession(session);
            }
        }
    }
}