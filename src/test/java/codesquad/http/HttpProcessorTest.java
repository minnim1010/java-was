package codesquad.http;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import codesquad.config.GlobalConfig;
import codesquad.http.handler.RequestHandlerResolver;
import codesquad.http.handler.StaticResourceRequestHandler;
import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;
import codesquad.http.parser.HttpParser;
import codesquad.http.property.HttpStatus;
import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Scanner;
import java.util.Set;
import java.util.TimeZone;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("HTTP 요청 처리 테스트")
class HttpProcessorTest {

    HttpParser httpParser = new HttpParser();
    RequestHandlerResolver requestHandlerResolver = new RequestHandlerResolver(Collections.emptyMap());
    StaticResourceRequestHandler staticResourceRequestHandler = new StaticResourceRequestHandler(Set.of("/index.html"));
    HttpRequestProcessor httpRequestProcessor = new HttpRequestProcessor(requestHandlerResolver,
            staticResourceRequestHandler);
    HttpProcessor httpProcessor = new HttpProcessor(httpParser, httpRequestProcessor);

    private void validateResponse(byte[] actualBytes, HttpResponse expectedResponse, HttpStatus expectedStatus) {
        Scanner scanner = new Scanner(new ByteArrayInputStream(actualBytes));
        String statusLine = scanner.nextLine();
        assertTrue(statusLine.contains(expectedStatus.getMessage()));

        boolean dateHeaderFound = false;
        while (scanner.hasNextLine()) {
            String headerLine = scanner.nextLine();
            if (headerLine.startsWith("Date: ")) {
                dateHeaderFound = true;
                String dateValue = headerLine.substring(6).trim();
                validateDateHeader(dateValue);
            }
            if (headerLine.isEmpty()) {
                break;
            }
        }

        assertTrue(dateHeaderFound, "Date header not found in response");
    }

    private void validateDateHeader(String dateValue) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", GlobalConfig.LOCALE);
        dateFormat.setTimeZone(TimeZone.getTimeZone(GlobalConfig.TIMEZONE));
        try {
            Date parsedDate = dateFormat.parse(dateValue);
            assertNotNull(parsedDate, "Date header is not in the correct format");
        } catch (Exception e) {
            fail("Date header is not in the correct format: " + e.getMessage());
        }
    }

    @Nested
    class HTTP_요청_처리를_성공적으로_수행하는_경우 {
        @Test
        void GET_요청_처리_시_200_OK_응답을_반환한다() throws Exception {
            String input = "GET / HTTP/1.1\r\nHost: localhost\r\nAccept: */*\n\r\n";

            byte[] result = httpProcessor.process(input);

            HttpResponse expectedResponse = new HttpResponse(HttpStatus.OK);
            validateResponse(result, expectedResponse, HttpStatus.OK);
        }
    }

    @Nested
    class HTTP_요청_처리를_실패하는_경우 {
        @Test
        void 유효하지_않은_요청_포맷이라면_400_BAD_REQUEST_응답을_반환한다() throws Exception {
            String input = "INVALID REQUEST";
            byte[] result = httpProcessor.process(input);

            HttpResponse expectedResponse = new HttpResponse(HttpStatus.BAD_REQUEST);
            validateResponse(result, expectedResponse, HttpStatus.BAD_REQUEST);
        }

        @Test
        void 리소스를_찾을_수_없는_경우_404_NOT_FOUND_응답을_반환한다() throws Exception {
            String input = "GET /nonexistent HTTP/1.1\r\nHost: localhost\r\n\r\n";
            byte[] result = httpProcessor.process(input);

            HttpResponse expectedResponse = new HttpResponse(HttpStatus.NOT_FOUND);
            validateResponse(result, expectedResponse, HttpStatus.NOT_FOUND);
        }

        @Test
        void 지원되지_않는_미디어_타입이라면_406_NOT_ACCEPTABLE_응답을_반환한다() throws Exception {
            String input = "POST / HTTP/1.1\r\nHost: localhost\r\n\r\n";
            HttpRequestProcessor faultyRequestProcessor = new HttpRequestProcessor(requestHandlerResolver,
                    staticResourceRequestHandler) {
                @Override
                public void processRequest(HttpRequest httpRequest, HttpResponse httpResponse) {
                    throw new UnsupportedOperationException();
                }
            };
            httpProcessor = new HttpProcessor(new HttpParser(), faultyRequestProcessor);

            byte[] result = httpProcessor.process(input);

            HttpResponse expectedResponse = new HttpResponse(HttpStatus.NOT_ACCEPTABLE);
            validateResponse(result, expectedResponse, HttpStatus.NOT_ACCEPTABLE);
        }

        @Test
        void 예기치_않은_예외가_발생하면_500_INTERNAL_SERVER_ERROR_응답을_반환한다() throws Exception {
            String input = "GET / HTTP/1.1\r\nHost: localhost\r\n\r\n";
            HttpRequestProcessor faultyRequestProcessor = new HttpRequestProcessor(requestHandlerResolver,
                    staticResourceRequestHandler) {
                @Override
                public void processRequest(HttpRequest httpRequest, HttpResponse httpResponse) {
                    throw new RuntimeException();
                }
            };
            httpProcessor = new HttpProcessor(new HttpParser(), faultyRequestProcessor);

            byte[] result = httpProcessor.process(input);

            HttpResponse expectedResponse = new HttpResponse(HttpStatus.INTERNAL_SERVER_ERROR);
            validateResponse(result, expectedResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
