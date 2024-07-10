package codesquad.http.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

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

    private long sessionTimeout = 1000L;
    private Session session;

    @Nested
    class 세션을_생성한다 {

        @Test
        void 유효한_세션을_생성할_수_있다() {
            session = new Session(sessionTimeout, "sessionId");

            assertThat(session.isValid()).isTrue();
        }
    }

    @Nested
    class 세션_속성을_설정한다 {

        @BeforeEach
        void setUp() {
            session = new Session(sessionTimeout, "sessionId");
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
            session = new Session(sessionTimeout, "sessionId");
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
            session = new Session(sessionTimeout, "sessionId");
        }

        @Test
        void 세션이_만료되면_예외를_던진다() throws InterruptedException {
            Thread.sleep(2000L);

            assertThatThrownBy(session::getSessionId)
                    .isInstanceOf(IllegalStateException.class);
            assertThat(session.isValid()).isFalse();
        }
    }

    @Nested
    class 세션의_남은_유효_시간을_계산한다 {

        @Test
        void 세션이_만료되기_전에_남은_시간을_확인한다() throws InterruptedException {
            Session session = new Session(sessionTimeout, "sessionId");
            Thread.sleep(900); // 0.9초 대기

            long remainSeconds = session.remainSeconds();

            assertThat(remainSeconds).isNotNegative();
        }

        @Test
        void 세션이_만료된_후에는_예외가_발생한다() throws InterruptedException {
            Session session = new Session(sessionTimeout, "sessionId");
            Thread.sleep(1500); // 1.5초 대기

            assertThatThrownBy(session::remainSeconds)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Session is expired");
        }
    }
}
