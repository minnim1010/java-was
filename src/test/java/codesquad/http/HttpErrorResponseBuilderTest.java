package codesquad.http;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;
import codesquad.utils.Fixture;
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
    void Internal_Server_Error_응답을_만든다() throws Exception {
        HttpRequest request = Fixture.createHttpGetRequest();
        HttpResponse response = new HttpResponse();

        HttpResponse result = httpErrorResponseBuilder.createServerErrorResponse(request, response);

        assertAll(() -> {
            assertEquals(500, result.getStatus().getCode());
            assertEquals("Internal Server Error", result.getStatus().getMessage());
        });
    }

    @Test
    void Bad_Request_응답을_만든다() throws Exception {
        HttpRequest request = Fixture.createHttpGetRequest();
        HttpResponse response = new HttpResponse();

        HttpResponse result = httpErrorResponseBuilder.createBadRequestResponse(request, response);

        assertAll(() -> {
            assertEquals(400, result.getStatus().getCode());
            assertEquals("Bad Request", result.getStatus().getMessage());
        });
    }

    @Test
    void Not_Found_응답을_만든다() throws Exception {
        HttpRequest request = Fixture.createHttpGetRequest();
        HttpResponse response = new HttpResponse();

        HttpResponse result = httpErrorResponseBuilder.createNotFoundResponse(request, response);

        assertAll(() -> {
            assertEquals(404, result.getStatus().getCode());
            assertEquals("Not Found", result.getStatus().getMessage());
        });
    }
}