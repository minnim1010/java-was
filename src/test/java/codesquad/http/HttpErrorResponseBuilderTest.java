package codesquad.http;

import static org.junit.jupiter.api.Assertions.assertEquals;

import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;
import codesquad.http.property.HttpMethod;
import codesquad.http.property.HttpVersion;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("HTTP 에러 응답 생성 테스트")
class HttpErrorResponseBuilderTest {

    private HttpErrorResponseBuilder httpErrorResponseBuilder = new HttpErrorResponseBuilder();

    @Test
    void Internal_Server_Error_응답을_만든다() throws IOException {
        HttpRequest request = new HttpRequest(HttpMethod.GET,
                "/index.html",
                HttpVersion.HTTP_1_1,
                new HashMap<>() {
                    {
                        put("Accept", "text/html");
                    }
                }, "");
        HttpResponse response = new HttpResponse();

        byte[] result = httpErrorResponseBuilder.createServerErrorResponse(request, response);

        String expected = "HTTP/1.1 500 Internal Server Error\r\n\r\n";
        assertEquals(0, Arrays.compare(expected.getBytes(), result));
    }

    @Test
    void Bad_Request_응답을_만든다() throws IOException {
        HttpRequest request = new HttpRequest(HttpMethod.GET,
                "/index.html",
                HttpVersion.HTTP_1_1,
                new HashMap<>() {
                    {
                        put("Accept", "text/html");
                    }
                }, "");
        HttpResponse response = new HttpResponse();

        byte[] result = httpErrorResponseBuilder.createBadRequestResponse(request, response);

        String expected = "HTTP/1.1 400 Bad Request\r\n\r\n";
        assertEquals(0, Arrays.compare(expected.getBytes(), result));
    }

    @Test
    void Not_Found_응답을_만든다() throws IOException {
        HttpRequest request = new HttpRequest(HttpMethod.GET,
                "/index.html",
                HttpVersion.HTTP_1_1,
                new HashMap<>() {
                    {
                        put("Accept", "text/html");
                    }
                }, "");
        HttpResponse response = new HttpResponse();

        byte[] result = httpErrorResponseBuilder.createNotFoundResponse(request, response);

        String expected = "HTTP/1.1 404 Not Found\r\n\r\n";
        assertEquals(0, Arrays.compare(expected.getBytes(), result));
    }
}