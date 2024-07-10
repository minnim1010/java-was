package codesquad.business.handler;

import static org.assertj.core.api.Assertions.assertThat;

import codesquad.business.model.User;
import codesquad.business.persistence.UserRepository;
import codesquad.environment.TestEnvironment;
import codesquad.http.cookie.Cookie;
import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;
import codesquad.http.property.HttpMethod;
import codesquad.http.property.HttpStatus;
import codesquad.http.property.HttpVersion;
import java.net.URI;
import java.net.URISyntaxException;
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
@DisplayName("로그인 테스트")
class LoginRequestHandlerTest extends TestEnvironment {

    private UserRepository userRepository;
    private LoginRequestHandler loginRequestHandler;

    @BeforeEach
    void setUp() {
        sessionManager.clear();
        userRepository = new UserRepository();
        loginRequestHandler = new LoginRequestHandler(userRepository);
    }

    @Nested
    class ProcessPost_테스트 {

        @Test
        void 유효한_사용자일_때_로그인_성공() throws URISyntaxException {
            // Given
            User user = new User("user1", "password", "John Doe", "john@example.com");
            userRepository.save(user);

            HttpRequest httpRequest = new HttpRequest(
                    HttpMethod.POST,
                    new URI("/user/login"),
                    Map.of("userId", "user1", "password", "password"),
                    HttpVersion.HTTP_1_1,
                    Collections.emptyMap(),
                    "userId=user1&password=password".getBytes()
            );
            HttpResponse httpResponse = new HttpResponse();

            // When
            loginRequestHandler.processPost(httpRequest, httpResponse);

            // Then
            assertThat(httpResponse.getStatus()).isEqualTo(HttpStatus.FOUND);
            assertThat(httpResponse.getHeader("Location")).isEqualTo("/index.html");

            Cookie cookie = httpResponse.getCookie("SID");
            assertThat(cookie).isNotNull();
        }

        @Test
        void 유효하지_않은_사용자일_때_로그인_실패() throws URISyntaxException {
            // Given
            HttpRequest httpRequest = new HttpRequest(
                    HttpMethod.POST,
                    new URI("/user/login"),
                    Map.of("userId", "user1", "password", "password"),
                    HttpVersion.HTTP_1_1,
                    Collections.emptyMap(),
                    "userId=user1&password=password".getBytes()
            );
            HttpResponse httpResponse = new HttpResponse();

            // When
            loginRequestHandler.processPost(httpRequest, httpResponse);

            // Then
            assertThat(httpResponse.getStatus()).isEqualTo(HttpStatus.FOUND);
            assertThat(httpResponse.getHeader("Location")).isEqualTo("/login_failed.html");
        }
    }
}
