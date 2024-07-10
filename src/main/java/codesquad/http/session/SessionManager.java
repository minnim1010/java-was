package codesquad.http.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private final int sessionPoolMaxSize;
    private final long sessionTimeout;
    private final SessionIdGenerator sessionIdGenerator;

    private final Map<String, Session> sessionPool = new ConcurrentHashMap<>();

    public SessionManager(int sessionPoolMaxSize,
                          long sessionTimeout,
                          SessionIdGenerator sessionIdGenerator) {
        this.sessionPoolMaxSize = sessionPoolMaxSize;
        this.sessionTimeout = sessionTimeout;
        this.sessionIdGenerator = sessionIdGenerator;
    }

    public Session createSession() {
        clearInvalidSessionIfFull();
        validateSessionPoolSize();

        Session session = new Session(sessionTimeout, sessionIdGenerator.generate());
        sessionPool.put(session.getSessionId(), session);

        return session;
    }

    private void clearInvalidSessionIfFull() {
        if (sessionPool.size() >= sessionPoolMaxSize) {
            removeInvalidSessions();
        }
    }

    private void validateSessionPoolSize() {
        if (sessionPool.size() >= sessionPoolMaxSize) {
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
