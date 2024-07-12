package codesquad.business.handler;

import static codesquad.http.header.HeaderField.CONTENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import codesquad.business.model.User;
import codesquad.business.persistence.UserRepository;
import codesquad.config.GlobalBeanContainer;
import codesquad.environment.TestEnvironment;
import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;
import codesquad.http.property.HttpMethod;
import codesquad.http.property.HttpStatus;
import codesquad.http.property.HttpVersion;
import codesquad.http.session.Session;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("사용자 목록 조회 및 렌더링 테스트")
class UserListHandlerTest extends TestEnvironment {

    private final GlobalBeanContainer globalBeanContainer = GlobalBeanContainer.getInstance();
    private final UserListHandler userListHandler = globalBeanContainer.userListRequestHandler();

    @Nested
    class 사용자_목록_조회_페이지를_요청한다 {

        @Test
        void 로그인_상태가_아니라면_로그인_페이지로_리다이렉트한다() throws URISyntaxException {
            // Given
            HttpRequest httpRequest = new HttpRequest(HttpMethod.GET,
                    new URI("/user/list"),
                    Collections.emptyMap(),
                    HttpVersion.HTTP_1_1,
                    Collections.emptyMap(),
                    new byte[0]);
            HttpResponse httpResponse = new HttpResponse();

            // When
            userListHandler.processGet(httpRequest, httpResponse);

            // Then
            assertThat(httpResponse.getStatus()).isEqualTo(HttpStatus.FOUND);
            assertThat(httpResponse.getHeader("Location")).isEqualTo("/login");
        }

        @Test
        void 로그인_상태라면_사용자_목록_조회_페이지를_반환한다() throws URISyntaxException {
            // Given
            UserRepository userRepository = GlobalBeanContainer.getInstance().userRepository();
            userRepository.save(new User("user1", "password1", "User One", "a"));
            userRepository.save(new User("user2", "password1", "User One", "b"));

            Session session = sessionManager.createSession();
            session.setAttribute("userId", "user1");
            HttpRequest httpRequest = new HttpRequest(HttpMethod.GET,
                    new URI("/user/list"),
                    Collections.emptyMap(),
                    HttpVersion.HTTP_1_1,
                    Collections.emptyMap(),
                    new byte[0]);
            httpRequest.setSession(session);
            HttpResponse httpResponse = new HttpResponse();

            // When
            userListHandler.processGet(httpRequest, httpResponse);

            // Then
            assertThat(httpResponse.getStatus()).isEqualTo(HttpStatus.OK);
            assertThat(httpResponse.getHeader(CONTENT_TYPE.getFieldName())).isEqualTo("text/html");

            String bodyContent = new String(httpResponse.getBody());
            assertAll(() -> {
                assertThat(bodyContent).contains("user1");
                assertThat(bodyContent).contains("user2");
            });
        }
    }
}
