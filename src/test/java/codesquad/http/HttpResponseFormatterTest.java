package codesquad.http;

import static org.junit.jupiter.api.Assertions.assertEquals;

import codesquad.http.property.HttpStatus;
import codesquad.http.property.HttpVersion;
import java.util.Collections;
import java.util.HashMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("HTTP 응답 포맷팅 테스트")
class HttpResponseFormatterTest {

    private HttpResponseFormatter formatter = new HttpResponseFormatter();

    @Nested
    class HTTP_응답_포맷을_만든다 {
        @Test
        void 헤더가_없는_경우() {
            HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_1_1, HttpStatus.OK, new HashMap<>(),
                    "Hello, World!");

            String result = formatter.formatResponse(httpResponse);

            String expected = "HTTP/1.1 200 OK\r\n\r\nHello, World!";
            assertEquals(expected, result);
        }

        @Test
        void 헤더가_있는_경우() {
            HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_1_1, HttpStatus.OK,
                    Collections.singletonMap("Content-Type", "text/html"), "Hello, World!");

            String result = formatter.formatResponse(httpResponse);

            String expected = "HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\nHello, World!";
            assertEquals(expected, result);
        }

        @Test
        void 여러_헤더가_있는_경우() {
            HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_1_1, HttpStatus.OK,
                    new HashMap<>() {{
                        put("Content-Type", "text/html");
                        put("Content-Length", "12");
                    }}, "Hello, World!");

            String result = formatter.formatResponse(httpResponse);

            String expected = "HTTP/1.1 200 OK\r\nContent-Length: 12\r\nContent-Type: text/html\r\n\r\nHello, World!";
            assertEquals(expected, result);
        }
    }

    @Test
    void Internal_Server_Error_응답을_만든다() {
        String result = formatter.createServerErrorResponse();

        String expected = "HTTP/1.1 500 Internal Server Error\r\n\r\n";
        assertEquals(expected, result);
    }

    @Test
    void Bad_Request_응답을_만든다() {
        String result = formatter.createBadRequestResponse();

        String expected = "HTTP/1.1 400 Bad Request\r\n\r\n";
        assertEquals(expected, result);
    }
}