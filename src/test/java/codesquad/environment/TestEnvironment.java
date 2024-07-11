package codesquad.environment;

import codesquad.http.session.SessionIdGenerator;
import codesquad.http.session.SessionManager;
import org.junit.jupiter.api.BeforeEach;

public abstract class TestEnvironment {

    protected static SessionManager sessionManager = SessionManager.createInstance(
            10,
            100000,
            new SessionIdGenerator() {

                @Override
                public String generate() {
                    return "session-id";
                }
            });

    @BeforeEach
    protected void clear() {
        sessionManager.clear();
    }
}
