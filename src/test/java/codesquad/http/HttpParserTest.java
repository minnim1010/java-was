package codesquad.http;

import static codesquad.utils.Fixture.createReaderWithInput;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import codesquad.http.message.HttpRequest;
import codesquad.http.parser.HttpParser;
import codesquad.http.property.HttpMethod;
import codesquad.http.property.HttpVersion;
import codesquad.socket.Reader;
import java.net.URI;
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
            Reader reader = createReaderWithInput("GET /index.html HTTP/1.1\r\n\r\n");

            HttpRequest result = parser.parse(reader);

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
            Reader reader = createReaderWithInput("GET /index.html HTTP/1.1\r\nHost: www.example.com\r\n\r\n");

            HttpRequest result = parser.parse(reader);

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
            Reader reader = createReaderWithInput("""
                    GET /index.html HTTP/1.1\r
                    Accept: text/html, application/xhtml+xml, application/xml;q=0.9, */*;q=0.8\r
                    Accept-Language: en-US,en;q=0.5\r
                    Accept-Encoding: gzip, deflate, br\r
                    Connection: keep-alive\r
                    Upgrade-Insecure-Requests: 1\r
                    \r
                    """);

            HttpRequest result = parser.parse(reader);

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
            Reader reader = createReaderWithInput("""
                    GET /index.html HTTP/1.1\r
                    Accept: text/html, application/xhtml+xml, application/xml;q=0.9, */*;q=0.8\r
                    Accept-Language: en-US,en;q=0.5\r
                    Accept-Encoding: gzip, deflate\r
                    Accept-Encoding: br\r
                    Connection: keep-alive\r
                    Upgrade-Insecure-Requests: 1\r
                    \r
                    """);

            HttpRequest result = parser.parse(reader);

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
            Reader reader = createReaderWithInput("""
                    GET /index.html?name=John HTTP/1.1\r
                    Host: www.example.com\r
                    Content-Length: 13\r
                    \r
                    Hello World!!""");

            HttpRequest httpRequest = parser.parse(reader);

            assertAll(
                    () -> assertNotNull(httpRequest),
                    () -> assertEquals(HttpMethod.GET, httpRequest.getMethod()),
                    () -> assertEquals(new URI("/index.html?name=John"), httpRequest.getUri()),
                    () -> assertEquals(HttpVersion.HTTP_1_1, httpRequest.getVersion()),
                    () -> assertEquals("www.example.com", httpRequest.getHeaders().get("Host")),
                    () -> assertEquals("13", httpRequest.getHeaders().get("Content-Length")),
                    () -> assertArrayEquals("Hello World!!".getBytes(), httpRequest.getBody())
            );
        }

        @Test
        void URL에_쿼리_파라미터가_있는_요청인_경우() {
            Reader reader = createReaderWithInput(
                    "GET /search?q=good&lang=en HTTP/1.1\r\nHost: www.example.com\r\n\r\n");

            HttpRequest result = parser.parse(reader);

            assertAll(
                    () -> assertNotNull(result),
                    () -> assertEquals("GET", result.getMethod().getDisplayName()),
                    () -> assertEquals("/search", result.getUri().getPath()),
                    () -> assertEquals("good", result.getQuery("q")),
                    () -> assertEquals("en", result.getQuery("lang")),
                    () -> assertEquals("www.example.com", result.getHeader("Host"))
            );
        }

        @Test
        void 바디에_쿼리_파라미터가_있는_요청인_경우() {
            Reader reader = createReaderWithInput("""
                    POST /search HTTP/1.1\r
                    Host: www.example.com\r
                    Content-Type: application/x-www-form-urlencoded\r
                    Content-Length: 21\r
                    \r
                    q=good&lang=ㅁㄴㅇㄹ@#$!!""");

            HttpRequest result = parser.parse(reader);

            assertAll(
                    () -> assertNotNull(result),
                    () -> assertEquals("POST", result.getMethod().getDisplayName()),
                    () -> assertEquals("/search", result.getUri().getPath()),
                    () -> assertEquals("good", result.getQuery("q")),
                    () -> assertEquals("ㅁㄴㅇㄹ@#$!!", result.getQuery("lang")),
                    () -> assertEquals("www.example.com", result.getHeader("Host"))
            );
        }

        @Test
        void 쿼리_파라미터값에_엠퍼센트가_있는_요청인_경우() {
            Reader reader = createReaderWithInput("""
                    POST /search HTTP/1.1\r
                    Host: www.example.com\r
                    Content-Type: application/x-www-form-urlencoded\r
                    Content-Length: 24\r
                    \r
                    q=good&lang=ㅁㄴㅇ%26ㄹ@#$!!""");

            HttpRequest result = parser.parse(reader);

            assertAll(
                    () -> assertNotNull(result),
                    () -> assertEquals("POST", result.getMethod().getDisplayName()),
                    () -> assertEquals("/search", result.getUri().getPath()),
                    () -> assertEquals("good", result.getQuery("q")),
                    () -> assertEquals("ㅁㄴㅇ&ㄹ@#$!!", result.getQuery("lang")),
                    () -> assertEquals("www.example.com", result.getHeader("Host"))
            );
        }
    }
}

