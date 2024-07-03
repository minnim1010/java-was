package codesquad.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import codesquad.error.ResourceNotFoundException;
import codesquad.http.property.HttpMethod;
import codesquad.http.property.HttpVersion;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("Http 요청 처리 테스트")
class HttpProcessorTest {

    private HttpProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new HttpProcessor();
    }

    @Nested
    class GET_요청을_처리한다 {
        @Test
        void 존재하는_정적_파일_요청을_처리할_수_있다() throws Exception {
            HttpRequest request = new HttpRequest(HttpMethod.GET,
                    "/index.html",
                    HttpVersion.HTTP_1_1,
                    new HashMap<>() {
                        {
                            put("Accept", "text/html");
                        }
                    }, "");

            HttpResponse response = processor.processRequest(request);

            assertEquals(200, response.status().getCode());
            assertEquals("text/html", response.headers().get("Content-Type"));
        }

        @Test
        void 존재하지_않는_정적_파일_요청_시_예외가_발생한다() {
            HttpRequest request = new HttpRequest(HttpMethod.GET,
                    "/nonexistent.html",
                    HttpVersion.HTTP_1_1,
                    new HashMap<>() {{
                        put("Accept", "text/html");
                    }}, "");

            assertThrows(ResourceNotFoundException.class, () -> processor.processRequest(request));
        }
    }
}