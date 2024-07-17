package codesquad.http;

import static codesquad.utils.Fixture.createReaderWithInput;
import static org.junit.jupiter.api.Assertions.assertTrue;

import codesquad.environment.TestEnvironment;
import codesquad.http.error.UnSupportedHttpMethodException;
import codesquad.http.handler.DynamicRequestHandlerResolver;
import codesquad.http.handler.StaticResourceRequestHandler;
import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;
import codesquad.socket.Reader;
import codesquad.socket.Writer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("HTTP 요청 처리 테스트")
class HttpProcessorTest extends TestEnvironment {

    private static DynamicRequestHandlerResolver dynamicRequestHandlerResolver = new DynamicRequestHandlerResolver(
            Collections.emptyMap());
    private static StaticResourceRequestHandler staticResourceRequestHandler = new StaticResourceRequestHandler(
            Set.of("/"),
            Set.of("/index.html"));
    private static HttpRequestProcessor httpRequestProcessor;
    private static HttpRequestPreprocessor httpRequestPreprocessor;
    private static HttpProcessor httpProcessor;

    @BeforeAll
    static void beforeAll() {
        httpRequestProcessor = new HttpRequestProcessor(dynamicRequestHandlerResolver, staticResourceRequestHandler);
        httpRequestPreprocessor = new HttpRequestPreprocessor(sessionManager);
        httpProcessor = new HttpProcessor(httpRequestPreprocessor, httpRequestProcessor);
    }
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
    }

    @AfterEach
    void tearDown() throws IOException {
        outputStream.close();
    }

    @Nested
    class HTTP_요청_처리를_성공적으로_수행하는_경우 {

        @Test
        void GET_요청_처리_시_200_OK_응답을_반환한다() throws Exception {
            Reader reader = createReaderWithInput("GET / HTTP/1.1\r\nHost: localhost\r\nAccept: */*\n\r\n");
            Writer writer = new Writer(outputStream);

            httpProcessor.process(reader, writer);

            assertTrue(outputStream.toString().startsWith("HTTP/1.1 200 OK"));
        }
    }

    @Nested
    class HTTP_요청_처리를_실패하는_경우 {
        @Test
        void 유효하지_않은_요청_포맷이라면_400_BAD_REQUEST_응답을_반환한다() throws Exception {
            Reader reader = createReaderWithInput("INVALID REQUEST");
            Writer writer = new Writer(outputStream);

            httpProcessor.process(reader, writer);

            assertTrue(outputStream.toString().startsWith("HTTP/1.1 400 Bad Request"));
        }

        @Test
        void 리소스를_찾을_수_없는_경우_404_NOT_FOUND_응답을_반환한다() throws Exception {
            Reader reader = createReaderWithInput("GET /notfound HTTP/1.1\r\nHost: localhost\r\n\r\n");
            Writer writer = new Writer(outputStream);

            httpProcessor.process(reader, writer);

            assertTrue(outputStream.toString().startsWith("HTTP/1.1 404 Not Found"));
        }

        @Test
        void 지원되지_않는_미디어_타입이라면_406_NOT_ACCEPTABLE_응답을_반환한다() throws Exception {
            Reader reader = createReaderWithInput("GET /notfound HTTP/1.1\r\nHost: localhost\r\n\r\n");
            Writer writer = new Writer(outputStream);
            HttpRequestProcessor faultyRequestProcessor = new HttpRequestProcessor(dynamicRequestHandlerResolver,
                    staticResourceRequestHandler) {
                @Override
                public void processRequest(HttpRequest httpRequest, HttpResponse httpResponse) {
                    throw new UnsupportedOperationException("Not Acceptable");
                }
            };
            HttpProcessor testHttpProcessor = new HttpProcessor(httpRequestPreprocessor, faultyRequestProcessor);

            testHttpProcessor.process(reader, writer);

            assertTrue(outputStream.toString().startsWith("HTTP/1.1 406 Not Acceptable"));
        }

        @Test
        void 지원되지_않는_HTTP_method라면_405_Method_Not_Allowed_응답을_반환한다() throws Exception {
            Reader reader = createReaderWithInput("GET / HTTP/1.1\r\nHost: localhost\r\n\r\n");
            Writer writer = new Writer(outputStream);
            HttpRequestProcessor faultyRequestProcessor = new HttpRequestProcessor(dynamicRequestHandlerResolver,
                    staticResourceRequestHandler) {
                @Override
                public void processRequest(HttpRequest httpRequest, HttpResponse httpResponse) {
                    throw new UnSupportedHttpMethodException("Method Not Allowed");
                }
            };
            HttpProcessor testHttpProcessor = new HttpProcessor(httpRequestPreprocessor, faultyRequestProcessor);

            testHttpProcessor.process(reader, writer);

            assertTrue(outputStream.toString().startsWith("HTTP/1.1 405 Method Not Allowed"));
        }

        @Test
        void 예기치_않은_예외가_발생하면_500_INTERNAL_SERVER_ERROR_응답을_반환한다() throws Exception {
            Reader reader = createReaderWithInput("GET / HTTP/1.1\r\nHost: localhost\r\n\r\n");
            Writer writer = new Writer(outputStream);
            HttpRequestProcessor faultyRequestProcessor = new HttpRequestProcessor(dynamicRequestHandlerResolver,
                    staticResourceRequestHandler) {
                @Override
                public void processRequest(HttpRequest httpRequest, HttpResponse httpResponse) {
                    throw new RuntimeException("Internal Server Error");
                }
            };
            HttpProcessor testHttpProcessor = new HttpProcessor(httpRequestPreprocessor, faultyRequestProcessor);

            testHttpProcessor.process(reader, writer);

            assertTrue(outputStream.toString().startsWith("HTTP/1.1 500 Internal Server Error"));
        }
    }
}
