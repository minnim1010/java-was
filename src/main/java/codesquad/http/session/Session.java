package codesquad.http.session;

import codesquad.config.GlobalConstants;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Session {

    private final GlobalConstants globalConstants;

    private final String sessionId;
    private final Instant createdAt;
    private final Map<String, String> attributes;
    private boolean isValid = true;

    public Session(GlobalConstants globalConstants,
                   String sessionId) {
        this.globalConstants = globalConstants;
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
        if (Instant.now().isAfter(createdAt.plusMillis(globalConstants.getSessionTimeout()))) {
            isValid = false;
            throw new IllegalStateException("Session is expired");
        }
    }
}
