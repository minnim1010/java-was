package codesquad.environment;

import codesquad.http.session.SessionIdGenerator;
import codesquad.http.session.SessionManager;

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
}
