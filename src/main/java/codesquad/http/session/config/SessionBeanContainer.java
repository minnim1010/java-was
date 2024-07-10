package codesquad.http.session.config;

import codesquad.http.session.SessionIdGenerator;
import codesquad.http.session.SessionManager;

public class SessionBeanContainer {

    private final SessionManager sessionManager;
    private final SessionIdGenerator sessionIdGenerator;

    public SessionBeanContainer(SessionConfig sessionConfig) {
        this.sessionManager = setSessionManager(sessionConfig);
        this.sessionIdGenerator = setSessionIdGenerator();
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public SessionIdGenerator getSessionIdGenerator() {
        return sessionIdGenerator;
    }

    protected SessionIdGenerator setSessionIdGenerator() {
        return new SessionIdGenerator();
    }

    protected SessionManager setSessionManager(SessionConfig sessionConfig) {
        return new SessionManager(sessionConfig, getSessionIdGenerator());
    }
}
