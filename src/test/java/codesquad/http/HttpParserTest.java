package codesquad.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("HTTP 요청 파싱 테스트")
class HttpParserTest {
    private HttpParser parser = new HttpParser();

    @Nested
    class HTTP_요청을_파싱한다 {
        @Test
        void 헤더가_있는_요청인_경우() {
            String httpRequestStr = "GET /index.html HTTP/1.1\r\nHost: www.example.com\r\n\r\n";

            Optional<MyHttpRequest> result = parser.parse(httpRequestStr);

            assertTrue(result.isPresent());
            assertEquals("GET", result.get().method());
            assertEquals("/index.html", result.get().path());
            assertEquals("HTTP/1.1", result.get().version().getVersion());
            assertEquals("www.example.com", result.get().getHeader("Host"));
        }

        @Test
        void 헤더가_없는_요청인_경우() {
            String httpRequestStr = "GET /index.html HTTP/1.1\r\n\r\n";

            Optional<MyHttpRequest> result = parser.parse(httpRequestStr);

            assertTrue(result.isPresent());
            assertEquals("GET", result.get().method());
            assertEquals("/index.html", result.get().path());
            assertEquals("HTTP/1.1", result.get().version().getVersion());
            assertTrue(result.get().headers().isEmpty());
        }

        @Test
        void 바디가_있는_요청인_경우() {
            String httpRequestStr = "POST /index.html HTTP/1.1\r\nHost: www.example.com\r\n\r\nHello, World!";

            Optional<MyHttpRequest> result = parser.parse(httpRequestStr);

            assertTrue(result.isPresent());
            assertEquals("POST", result.get().method());
            assertEquals("/index.html", result.get().path());
            assertEquals("HTTP/1.1", result.get().version().getVersion());
            assertEquals("www.example.com", result.get().getHeader("Host"));
            assertEquals("Hello, World!", result.get().body());
        }
    }
}