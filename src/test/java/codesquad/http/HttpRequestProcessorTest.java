package codesquad.http;

import static codesquad.http.header.HeaderField.CONTENT_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import codesquad.error.ResourceNotFoundException;
import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;
import codesquad.utils.Fixture;
import java.net.URISyntaxException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("HTTP API 실행 및 리소스 반환 테스트")
class HttpRequestProcessorTest {

    private HttpRequestProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new HttpRequestProcessor();
    }

    @Nested
    class GET_요청을_처리한다 {
        @Test
        void 존재하는_정적_파일_요청을_처리할_수_있다() throws Exception {
            HttpRequest request = Fixture.createHttpGetRequest();
            HttpResponse response = new HttpResponse();

            processor.processRequest(request, response);

            assertEquals("HTTP/1.1", response.getVersion().getDisplayName());
            assertEquals(200, response.getStatus().getCode());
            assertEquals("text/html", response.getHeader(CONTENT_TYPE.getFieldName()));
        }

        @Test
        void 디렉토리만_명시된_uri를_가진_요청인_경우_해당_디렉토리의_디폴트_파일을_반환하여_처리할_수_있다() throws Exception {
            HttpRequest request = Fixture.createHttpGetRequest("/");
            HttpResponse response = new HttpResponse();

            processor.processRequest(request, response);

            assertEquals("HTTP/1.1", response.getVersion().getDisplayName());
            assertEquals(200, response.getStatus().getCode());
            assertEquals("text/html", response.getHeader(CONTENT_TYPE.getFieldName()));
        }

        @Test
        void 존재하지_않는_정적_파일_요청_시_예외가_발생한다() throws URISyntaxException {
            HttpRequest request = Fixture.createHttpGetRequest("/not_found_really.html");
            HttpResponse response = new HttpResponse();

            assertThrows(ResourceNotFoundException.class, () -> processor.processRequest(request, response));
        }
    }
}