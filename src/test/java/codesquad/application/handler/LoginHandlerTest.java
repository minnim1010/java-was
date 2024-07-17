package codesquad.application.handler;

import static org.assertj.core.api.Assertions.assertThat;

import codesquad.application.infrastructure.InMemoryUserRepository;
import codesquad.application.model.User;
import codesquad.application.persistence.UserRepository;
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
class LoginHandlerTest extends TestEnvironment {

    private UserRepository userRepository;
    private LoginHandler loginHandler;

    @BeforeEach
    void setUp() {
        userRepository = new InMemoryUserRepository();
        loginHandler = new LoginHandler(userRepository);
    }

    @Nested
    class 로그인한다 {

        @Test
        void 회원가입한_사용자라면_로그인할_수_있다() throws URISyntaxException {
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
            loginHandler.processPost(httpRequest, httpResponse);

            // Then
            assertThat(httpResponse.getStatus()).isEqualTo(HttpStatus.FOUND);
            assertThat(httpResponse.getHeader("Location")).isEqualTo("/index.html");

            Cookie cookie = httpResponse.getCookie("SID");
            assertThat(cookie).isNotNull();
        }

        @Test
        void 비밀번호가_틀렸다면_로그인할_수_없다() throws URISyntaxException {
            // Given
            User user = new User("user1", "password", "John Doe", "john@example.com");
            userRepository.save(user);

            HttpRequest httpRequest = new HttpRequest(
                    HttpMethod.POST,
                    new URI("/user/login"),
                    Map.of("userId", "user1", "password", "invalid password"),
                    HttpVersion.HTTP_1_1,
                    Collections.emptyMap(),
                    "userId=user1&password=password".getBytes()
            );
            HttpResponse httpResponse = new HttpResponse();

            // When
            loginHandler.processPost(httpRequest, httpResponse);

            // Then
            assertThat(httpResponse.getStatus()).isEqualTo(HttpStatus.FOUND);
            assertThat(httpResponse.getHeader("Location")).isEqualTo("/user/login_failed.html");
        }

        @Test
        void 회원가입하지_않은_사용자라면_로그인할_수_없다() throws URISyntaxException {
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
            loginHandler.processPost(httpRequest, httpResponse);

            // Then
            assertThat(httpResponse.getStatus()).isEqualTo(HttpStatus.FOUND);
            assertThat(httpResponse.getHeader("Location")).isEqualTo("/user/login_failed.html");
        }
    }
}
