package codesquad.http;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import codesquad.http.message.HttpRequest;
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
        void 헤더를_가지지_않는_요청인_경우() {
            String httpRequestStr = "GET /index.html HTTP/1.1\r\n\r\n";

            HttpRequest result = parser.parse(httpRequestStr);

            assertAll(
                    () -> assertNotNull(result),
                    () -> assertEquals("GET", result.getMethod().getDisplayName()),
                    () -> assertEquals("/index.html", result.getUri().getPath()),
                    () -> assertEquals("HTTP/1.1", result.getVersion().getDisplayName()),
                    () -> assertTrue(result.getHeaders().isEmpty())
            );
        }

        @Test
        void 헤더를_가진_요청인_경우() {
            String httpRequestStr = "GET /index.html HTTP/1.1\r\nHost: www.example.com\r\n\r\n";

            HttpRequest result = parser.parse(httpRequestStr);

            assertAll(
                    () -> assertNotNull(result),
                    () -> assertEquals("GET", result.getMethod().getDisplayName()),
                    () -> assertEquals("/index.html", result.getUri().getPath()),
                    () -> assertEquals("HTTP/1.1", result.getVersion().getDisplayName()),
                    () -> assertEquals("www.example.com", result.getHeader("Host"))
            );
        }

        @Test
        void 여러_값을_가지는_헤더를_가진_요청인_경우() {
            String httpRequestStr = """
                    GET /index.html HTTP/1.1\r
                    Accept: text/html, application/xhtml+xml, application/xml;q=0.9, */*;q=0.8\r
                    Accept-Language: en-US,en;q=0.5\r
                    Accept-Encoding: gzip, deflate, br\r
                    Connection: keep-alive\r
                    Upgrade-Insecure-Requests: 1\r
                    \r
                    """;

            HttpRequest result = parser.parse(httpRequestStr);

            assertAll(
                    () -> assertNotNull(result),
                    () -> assertEquals("GET", result.getMethod().getDisplayName()),
                    () -> assertEquals("/index.html", result.getUri().getPath()),
                    () -> assertEquals("HTTP/1.1", result.getVersion().getDisplayName()),
                    () -> assertEquals("text/html, application/xhtml+xml, application/xml;q=0.9, */*;q=0.8",
                            result.getHeader("Accept")),
                    () -> assertEquals("en-US,en;q=0.5", result.getHeader("Accept-Language")),
                    () -> assertEquals("gzip, deflate, br", result.getHeader("Accept-Encoding")),
                    () -> assertEquals("keep-alive", result.getHeader("Connection")),
                    () -> assertEquals("1", result.getHeader("Upgrade-Insecure-Requests"))
            );
        }

        @Test
        void 중복되는_타입이_있는_헤더_가진_요청인_경우() {
            String httpRequestStr = """
                    GET /index.html HTTP/1.1\r
                    Accept: text/html, application/xhtml+xml, application/xml;q=0.9, */*;q=0.8\r
                    Accept-Language: en-US,en;q=0.5\r
                    Accept-Encoding: gzip, deflate\r
                    Accept-Encoding: br\r
                    Connection: keep-alive\r
                    Upgrade-Insecure-Requests: 1\r
                    \r
                    """;

            HttpRequest result = parser.parse(httpRequestStr);

            assertAll(
                    () -> assertNotNull(result),
                    () -> assertEquals("GET", result.getMethod().getDisplayName()),
                    () -> assertEquals("/index.html", result.getUri().getPath()),
                    () -> assertEquals("HTTP/1.1", result.getVersion().getDisplayName()),
                    () -> assertEquals("text/html, application/xhtml+xml, application/xml;q=0.9, */*;q=0.8",
                            result.getHeader("Accept")),
                    () -> assertEquals("en-US,en;q=0.5", result.getHeader("Accept-Language")),
                    () -> assertEquals("gzip, deflate, br", result.getHeader("Accept-Encoding")),
                    () -> assertEquals("keep-alive", result.getHeader("Connection")),
                    () -> assertEquals("1", result.getHeader("Upgrade-Insecure-Requests"))
            );
        }

        @Test
        void 바디가_있는_요청인_경우() {
            String httpRequestStr = "POST /index.html HTTP/1.1\r\nHost: www.example.com\r\n\r\nHello, World!";

            HttpRequest result = parser.parse(httpRequestStr);

            assertAll(
                    () -> assertNotNull(result),
                    () -> assertEquals("POST", result.getMethod().getDisplayName()),
                    () -> assertEquals("/index.html", result.getUri().getPath()),
                    () -> assertEquals("HTTP/1.1", result.getVersion().getDisplayName()),
                    () -> assertEquals("www.example.com", result.getHeader("Host")),
                    () -> assertEquals("Hello, World!", result.getBody())
            );
        }
    }
}