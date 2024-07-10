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
@DisplayName("여러 세션을 관리하는 테스트")
class SessionManagerTest {

    private final SessionConfig sessionConfig = new SessionConfig() {

        @Override
        protected long setSessionTimeout() {
            return 1000L;
        }

        @Override
        protected int setSessionPoolMaxSize() {
            return 2;
        }
    };

    private final SessionIdGenerator sessionIdGenerator = new SessionIdGenerator() {
        private int id = 0;

        @Override
        public String generate() {
            return String.valueOf(id++);
        }
    };

    private SessionManager sessionManager;

    @Nested
    @DisplayName("세션 생성 테스트")
    class 세션을_생성한다 {

        @BeforeEach
        void setUp() {
            sessionManager = new SessionManager(sessionConfig, sessionIdGenerator);
        }

        @Test
        void 새로운_세션을_생성한다() {
            Session session = sessionManager.createSession();

            assertThat(session).isNotNull();
        }

        @Test
        void 세션_풀_사이즈_초과시_예외를_던진다() {
            sessionManager.createSession();
            sessionManager.createSession();

            assertThatThrownBy(sessionManager::createSession)
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        void 세션_풀_사이즈_초과하고_새로운_세션을_생성할_시_유효하지_않은_세션을_제거한다() throws InterruptedException {
            Session session1 = sessionManager.createSession();
            String invalidatedSessionId = session1.getSessionId();
            session1.invalidate();
            sessionManager.createSession();

            Thread.sleep(1500);
            Session session3 = sessionManager.createSession();

            assertAll(
                    () -> assertThat(sessionManager.getSession(invalidatedSessionId)).isNull(),
                    () -> assertThat(sessionManager.getSession(session3.getSessionId())).isNotNull()
            );
        }
    }

    @Nested
    @DisplayName("세션 조회 및 제거 테스트")
    class GetAndRemoveSessionTest {

        @BeforeEach
        void setUp() {
            sessionManager = new SessionManager(sessionConfig, sessionIdGenerator);
        }

        @Test
        void 세션을_조회한다() {
            Session session = sessionManager.createSession();
            String sessionId = session.getSessionId();

            assertThat(sessionManager.getSession(sessionId)).isNotNull();
        }

        @Test
        void 세션을_제거한다() {
            Session session = sessionManager.createSession();
            String sessionId = session.getSessionId();
            sessionManager.removeSession(sessionId);

            assertThat(sessionManager.getSession(sessionId)).isNull();
        }
    }
}
