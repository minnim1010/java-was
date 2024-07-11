package codesquad.business.handler;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import codesquad.config.GlobalBeanContainer;
import codesquad.http.error.UnSupportedHttpMethodException;
import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;
import codesquad.http.property.HttpMethod;
import codesquad.http.property.HttpStatus;
import codesquad.http.property.HttpVersion;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("회원가입 테스트")
class UserHandlerTest {

    private final GlobalBeanContainer globalBeanContainer = GlobalBeanContainer.getInstance();
    private final UserHandler userRequestHandler = globalBeanContainer.userRequestHandler();

    @Nested
    class POST_요청_시 {

        @Test
        void 유효한_회원가입_쿼리_파라미터가_있는_경우_초기_페이지로_리다이렉트한다() throws Exception {
            HttpRequest httpRequest = new HttpRequest(HttpMethod.POST,
                    new URI("/create"),
                    Map.of("userId", "1", "name", "JohnDoe", "email", "johndoe@example.com", "password", "secret"),
                    HttpVersion.HTTP_1_1,
                    Collections.emptyMap(),
                    new byte[0]);
            HttpResponse httpResponse = new HttpResponse();

            userRequestHandler.processPost(httpRequest, httpResponse);

            assertAll(
                    () -> assertEquals(HttpStatus.FOUND, httpResponse.getStatus()),
                    () -> assertEquals("/index.html", httpResponse.getHeader("Location"))
            );
        }
    }

    @Nested
    class 지원하지_않는_HTTP_METHOD_예외가_발생한다 {

        @Test
        void GET_요청하는_경우() throws Exception {
            HttpRequest httpRequest = new HttpRequest(HttpMethod.GET,
                    new URI("/create"),
                    Map.of("userId", "1", "name", "JohnDoe", "email", "johndoe@example.com", "password", "secret"),
                    HttpVersion.HTTP_1_1,
                    Collections.emptyMap(),
                    new byte[0]);
            HttpResponse httpResponse = new HttpResponse();

            assertThrows(UnSupportedHttpMethodException.class,
                    () -> userRequestHandler.processGet(httpRequest, httpResponse));
        }
    }

}