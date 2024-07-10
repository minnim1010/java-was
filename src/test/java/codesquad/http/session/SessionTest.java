package codesquad.http.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import codesquad.http.session.config.SessionConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("세션 테스트")
class SessionTest {

    private final SessionConfig mockSessionConfig = new SessionConfig() {
        @Override
        protected long setSessionTimeout() {
            return 1000L;
        }
    };

    private Session session;

    @Nested
    class 세션을_생성한다 {

        @Test
        void 유효한_세션을_생성할_수_있다() {
            session = new Session(mockSessionConfig, "sessionId");

            assertThat(session.isValid()).isTrue();
        }
    }

    @Nested
    class 세션_속성을_설정한다 {

        @BeforeEach
        void setUp() {
            session = new Session(mockSessionConfig, "sessionId");
        }

        @Test
        void 속성을_설정할_수_있다() {
            session.setAttribute("key1", "value1");

            assertThat(session.getAttribute("key1")).isEqualTo("value1");
        }

        @Test
        void 유효하지_않은_속성을_가져오면_null을_반환한다() {
            assertThat(session.getAttribute("nonexistentKey")).isNull();
        }
    }

    @Nested
    class 세션을_무효화한다 {

        @BeforeEach
        void setUp() {
            session = new Session(mockSessionConfig, "sessionId");
        }

        @Test
        void 세션을_무효화할_수_있다() {
            session.invalidate();

            assertAll(
                    () -> assertThat(session.isValid()).isFalse(),
                    () -> assertThatThrownBy(session::getSessionId)
                            .isInstanceOf(IllegalStateException.class)
            );
        }
    }

    @Nested
    @DisplayName("세션 만료 테스트")
    class 세션을_만료한다 {

        @BeforeEach
        void setUp() {
            SessionConfig timeoutMockSessionConfig = new SessionConfig() {
                @Override
                protected long setSessionTimeout() {
                    return 1000L;
                }
            };
            session = new Session(timeoutMockSessionConfig, "sessionId");
        }

        @Test
        void 세션이_만료되면_예외를_던진다() throws InterruptedException {
            Thread.sleep(2000L);

            assertThatThrownBy(session::getSessionId)
                    .isInstanceOf(IllegalStateException.class);
            assertThat(session.isValid()).isFalse();
        }
    }
}
