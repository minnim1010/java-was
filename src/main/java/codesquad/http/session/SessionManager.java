package codesquad.http.session;

import codesquad.http.session.config.SessionConfig;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private final SessionConfig sessionConfig;
    private final SessionIdGenerator sessionIdGenerator;
    private final Map<String, Session> sessionPool = new ConcurrentHashMap<>();

    public SessionManager(SessionConfig sessionConfig,
                          SessionIdGenerator sessionIdGenerator) {
        this.sessionConfig = sessionConfig;
        this.sessionIdGenerator = sessionIdGenerator;
    }

    public Session createSession() {
        clearInvalidSessionIfFull();
        validateSessionPoolSize();

        Session session = new Session(sessionConfig, sessionIdGenerator.generate());
        sessionPool.put(session.getSessionId(), session);

        return session;
    }

    private void clearInvalidSessionIfFull() {
        if (sessionPool.size() >= sessionConfig.getSessionPoolMaxSize()) {
            removeInvalidSessions();
        }
    }

    private void validateSessionPoolSize() {
        if (sessionPool.size() >= sessionConfig.getSessionPoolMaxSize()) {
            throw new IllegalStateException("Session Pool is full");
        }
    }

    private void removeInvalidSessions() {
        sessionPool.entrySet().removeIf(entry -> !entry.getValue().isValid());
    }

    public Session getSession(String sessionId) {
        return sessionPool.get(sessionId);
    }

    public void removeSession(String sessionId) {
        sessionPool.remove(sessionId);
    }
}
