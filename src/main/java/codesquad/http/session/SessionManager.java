package codesquad.http.session;

import codesquad.config.GlobalConstants;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private final GlobalConstants globalConstants;
    private final SessionIdGenerator sessionIdGenerator;
    private final Map<String, Session> sessionPool = new ConcurrentHashMap<>();

    public SessionManager(GlobalConstants globalConstants,
                          SessionIdGenerator sessionIdGenerator) {
        this.globalConstants = globalConstants;
        this.sessionIdGenerator = sessionIdGenerator;
    }

    public Session createSession() {
        clearInvalidSessionIfFull();
        validateSessionPoolSize();

        Session session = new Session(globalConstants, sessionIdGenerator.generate());
        sessionPool.put(session.getSessionId(), session);

        return session;
    }

    private void clearInvalidSessionIfFull() {
        if (sessionPool.size() >= globalConstants.getSessionPoolMaxSize()) {
            removeInvalidSessions();
        }
    }

    private void validateSessionPoolSize() {
        if (sessionPool.size() >= globalConstants.getSessionPoolMaxSize()) {
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
