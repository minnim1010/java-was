package codesquad.business.processor;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import codesquad.error.UnSupportedHttpMethodException;
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

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("UserProcessor 테스트")
class UserProcessorTest {

    private final UserProcessor userProcessor = new UserProcessor();

    @Nested
    class POST_요청_시 {

        @Test
        void 유효한_회원가입_쿼리_파라미터가_있는_경우_초기_페이지로_리다이렉트한다() throws Exception {
            HttpRequest httpRequest = new HttpRequest(HttpMethod.POST,
                    new URI("/create"),
                    Map.of("userId", "1", "name", "JohnDoe", "email", "johndoe@example.com", "password", "secret"),
                    HttpVersion.HTTP_1_1,
                    Collections.emptyMap(),
                    "");
            HttpResponse httpResponse = new HttpResponse();

            userProcessor.processPost(httpRequest, httpResponse);

            assertAll(
                    () -> assertEquals(HttpStatus.MOVED_PERMANENTLY, httpResponse.getStatus()),
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
                    "");
            HttpResponse httpResponse = new HttpResponse();

            assertThrows(UnSupportedHttpMethodException.class,
                    () -> userProcessor.processGet(httpRequest, httpResponse));
        }
    }
}
