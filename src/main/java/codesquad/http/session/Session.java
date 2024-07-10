package codesquad.http.session;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Session {

    private final long sessionTimeout;

    private final String sessionId;
    private final Instant createdAt;
    private final Map<String, String> attributes;
    private boolean isValid = true;

    public Session(long sessionTimeout,
                   String sessionId) {
        this.sessionTimeout = sessionTimeout;
        this.sessionId = sessionId;
        this.createdAt = Instant.now();
        this.attributes = new ConcurrentHashMap<>();
    }

    public String getSessionId() {
        validSession();
        return sessionId;
    }

    public String getAttribute(String key) {
        validSession();
        return attributes.get(key);
    }

    public void setAttribute(String key, String value) {
        validSession();
        attributes.put(key, value);
    }

    public void invalidate() {
        validSession();
        isValid = false;
    }

    public boolean isValid() {
        return isValid;
    }

    private void validSession() {
        if (!isValid) {
            throw new IllegalStateException("Session is invalidated");
        }
        if (Instant.now().isAfter(createdAt.plusMillis(sessionTimeout))) {
            isValid = false;
            throw new IllegalStateException("Session is expired");
        }
    }
}
