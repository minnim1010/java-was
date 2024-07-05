package codesquad.http.message;

import static org.junit.jupiter.api.Assertions.assertEquals;

import codesquad.http.property.HttpStatus;
import codesquad.http.property.HttpVersion;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("HTTP 응답 테스트")
class HttpResponseTest {

    @Nested
    class HTTP_응답_포맷을_만든다 {
        @Test
        void 헤더가_없는_경우() throws IOException {
            HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_1_1, HttpStatus.OK, new HashMap<>(),
                    "Hello, World!".getBytes());

            byte[] result = httpResponse.format();

            String expected = "HTTP/1.1 200 OK\r\n\r\nHello, World!";
            assertEquals(0, Arrays.compare(expected.getBytes(), result));
        }

        @Test
        void 헤더가_있는_경우() throws IOException {
            HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_1_1, HttpStatus.OK,
                    Collections.singletonMap("Content-Type", "text/html"), "Hello, World!".getBytes());

            byte[] result = httpResponse.format();

            String expected = "HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\nHello, World!";
            assertEquals(0, Arrays.compare(expected.getBytes(), result));
        }

        @Test
        void 여러_헤더가_있는_경우() throws IOException {
            HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_1_1, HttpStatus.OK,
                    new HashMap<>() {{
                        put("Content-Type", "text/html");
                        put("Content-Length", "12");
                    }}, "Hello, World!".getBytes());

            byte[] result = httpResponse.format();

            String expected = "HTTP/1.1 200 OK\r\nContent-Length: 12\r\nContent-Type: text/html\r\n\r\nHello, World!";
            assertEquals(0, Arrays.compare(expected.getBytes(), result));
        }
    }
}
