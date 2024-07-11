package codesquad.http;

import static org.assertj.core.api.Assertions.assertThat;

import codesquad.environment.TestEnvironment;
import codesquad.http.message.HttpRequest;
import codesquad.http.parser.HttpParser;
import codesquad.socket.Reader;
import codesquad.utils.Fixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("HTTP 요청 전처리(파싱, 세션 로드) 테스트")
class HttpRequestPreprocessorTest extends TestEnvironment {

    private HttpParser httpParser;
    private HttpRequestPreprocessor preprocessor;
    private Reader reader;

    @BeforeEach
    void setUp() {
        httpParser = new HttpParser();
        preprocessor = new HttpRequestPreprocessor(httpParser, sessionManager);
    }

    @Nested
    class HttpRequest_처리_테스트 {

        @Test
        void 쿠키에_세션ID가_있다면_세션ID를_요청에_설정한다() {
            sessionManager.createSession();
            reader = Fixture.createReaderWithInput("GET / HTTP/1.1\r\nCookie: SID=session-id\r\n\r\n");

            HttpRequest request = preprocessor.process(reader);

            assertThat(request.getSession()).isNotNull();
        }

        @Test
        void HTTP_요청에_쿠키가_없으면_세션을_설정하지_않는다() {
            reader = Fixture.createReaderWithInput("GET / HTTP/1.1\r\n\r\n");

            HttpRequest request = preprocessor.process(reader);

            assertThat(request.getSession()).isNull();
        }
    }
}
