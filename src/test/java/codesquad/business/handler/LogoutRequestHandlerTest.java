package codesquad.business.handler;

import static org.assertj.core.api.Assertions.assertThat;

import codesquad.environment.TestEnvironment;
import codesquad.http.cookie.Cookie;
import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;
import codesquad.http.property.HttpMethod;
import codesquad.http.property.HttpStatus;
import codesquad.http.property.HttpVersion;
import codesquad.http.session.Session;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("로그아웃 테스트")
class LogoutRequestHandlerTest extends TestEnvironment {

    private LogoutRequestHandler logoutRequestHandler;

    @BeforeEach
    void setUp() {
        logoutRequestHandler = new LogoutRequestHandler();
    }

    @Nested
    class 로그아웃한다 {

        @Test
        void 쿠키가_없으면_UNAUTHORIZED_응답을_반환한다() throws Exception {
            // Given
            sessionManager.createSession();
            HttpRequest httpRequest = new HttpRequest(
                    HttpMethod.GET,
                    new URI("/user/logout"),
                    Collections.emptyMap(),
                    HttpVersion.HTTP_1_1,
                    Collections.emptyMap(),
                    "".getBytes()
            );
            HttpResponse httpResponse = new HttpResponse();

            // When
            logoutRequestHandler.processGet(httpRequest, httpResponse);

            // Then
            assertThat(httpResponse.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        @Test
        void 세션이_없으면_UNAUTHORIZED_응답을_반환한다() throws Exception {
            // Given
            HttpRequest httpRequest = new HttpRequest(
                    HttpMethod.GET,
                    new URI("/user/logout"),
                    Collections.emptyMap(),
                    HttpVersion.HTTP_1_1,
                    Map.of("Cookie", "SID=session-id"),
                    "".getBytes()
            );
            HttpResponse httpResponse = new HttpResponse();

            // When
            logoutRequestHandler.processGet(httpRequest, httpResponse);

            // Then
            assertThat(httpResponse.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        @Test
        void 세션이_있으면_로그아웃_처리하고_REDIRECT_응답을_반환한다() throws Exception {
            Session session = sessionManager.createSession();
            String sessionId = session.getSessionId();
            HttpRequest httpRequest = new HttpRequest(
                    HttpMethod.GET,
                    new URI("/user/logout"),
                    Collections.emptyMap(),
                    HttpVersion.HTTP_1_1,
                    Map.of("Cookie", "SID=session-id"),
                    "".getBytes()
            );
            httpRequest.setSession(session);
            HttpResponse httpResponse = new HttpResponse();

            logoutRequestHandler.processGet(httpRequest, httpResponse);

            assertThat(httpResponse.getStatus()).isEqualTo(HttpStatus.FOUND);
            assertThat(httpResponse.getHeader("Location")).isEqualTo("/index.html");

            Cookie cookie = httpResponse.getCookie("SID");
            assertThat(cookie).isNotNull();
            assertThat(cookie.getValue()).isEqualTo(sessionId);
            assertThat(cookie.getMaxAge()).isEqualTo(-1);
        }
    }
}
