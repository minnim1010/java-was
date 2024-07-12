package codesquad.http;

import static codesquad.http.header.HeaderField.CONTENT_TYPE;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import codesquad.http.error.ResourceNotFoundException;
import codesquad.http.handler.AbstractDynamicRequestHandler;
import codesquad.http.handler.DynamicRequestHandlerResolver;
import codesquad.http.handler.StaticResourceRequestHandler;
import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;
import codesquad.http.property.HttpStatus;
import codesquad.template.NodeProcessor;
import codesquad.template.TemplateEngine;
import codesquad.utils.Fixture;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
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

    private HttpRequestProcessor httpRequestProcessor;
    private final TemplateEngine templateEngine = TemplateEngine.createInstance(new NodeProcessor());

    @Nested
    class 정적_파일_요청을_처리한다 {

        @BeforeEach
        void setUp() {
            DynamicRequestHandlerResolver dynamicRequestHandlerResolver = new DynamicRequestHandlerResolver(
                    Collections.emptyMap());
            StaticResourceRequestHandler staticResourceRequestHandler = new StaticResourceRequestHandler(
                    Set.of("/index.html"),
                    Set.of("index.html"));
            httpRequestProcessor = new HttpRequestProcessor(dynamicRequestHandlerResolver,
                    staticResourceRequestHandler);
        }

        @Test
        void 존재하는_정적_파일_요청을_처리할_수_있다() throws Exception {
            HttpRequest request = Fixture.createHttpGetRequest();
            HttpResponse response = new HttpResponse();

            httpRequestProcessor.processRequest(request, response);

            assertEquals("HTTP/1.1", response.getVersion().getDisplayName());
            assertEquals(200, response.getStatus().getCode());
            assertEquals("text/html", response.getHeader(CONTENT_TYPE.getFieldName()));
        }

        @Test
        void 디렉토리만_명시된_uri를_가진_요청인_경우_해당_디렉토리의_디폴트_파일을_반환하여_처리할_수_있다() throws Exception {
            HttpRequest request = Fixture.createHttpGetRequest("/");
            HttpResponse response = new HttpResponse();

            httpRequestProcessor.processRequest(request, response);

            assertEquals("HTTP/1.1", response.getVersion().getDisplayName());
            assertEquals(200, response.getStatus().getCode());
            assertEquals("text/html", response.getHeader(CONTENT_TYPE.getFieldName()));
        }

        @Test
        void 존재하지_않는_정적_파일_요청_시_예외가_발생한다() throws URISyntaxException {
            HttpRequest request = Fixture.createHttpGetRequest("/not_found_really.html");
            HttpResponse response = new HttpResponse();

            assertThrows(ResourceNotFoundException.class, () -> httpRequestProcessor.processRequest(request, response));
        }
    }

    @Nested
    class HTTP_API_요청을_성공적으로_처리한다 {

        @BeforeEach
        void setUp() {
            DynamicRequestHandlerResolver dynamicRequestHandlerResolver = new DynamicRequestHandlerResolver(
                    Map.of("/test", new TestDynamicRequestHandler()));
            StaticResourceRequestHandler staticResourceRequestHandler = new StaticResourceRequestHandler(
                    Set.of("/"),
                    Set.of("/index.html"));
            httpRequestProcessor = new HttpRequestProcessor(dynamicRequestHandlerResolver,
                    staticResourceRequestHandler);
        }

        @Test
        void GET_요청이_정상적으로_처리된다() throws Exception {
            HttpRequest httpRequest = Fixture.createHttpGetRequest("/test");
            HttpResponse httpResponse = new HttpResponse();

            httpRequestProcessor.processRequest(httpRequest, httpResponse);

            assertAll(
                    () -> assertNotNull(httpResponse),
                    () -> assertEquals(HttpStatus.OK, httpResponse.getStatus()),
                    () -> assertEquals("GET request processed", new String(httpResponse.getBody()))
            );
        }

        public static class TestDynamicRequestHandler extends AbstractDynamicRequestHandler {

            @Override
            public void processGet(HttpRequest httpRequest, HttpResponse httpResponse) {
                httpResponse.setStatus(HttpStatus.OK);
                httpResponse.setBody("GET request processed".getBytes());
            }

            @Override
            public void processPost(HttpRequest httpRequest, HttpResponse httpResponse) {
                httpResponse.setStatus(HttpStatus.OK);
                httpResponse.setBody("POST request processed".getBytes());
            }
        }
    }
}