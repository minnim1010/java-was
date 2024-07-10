package codesquad.http.session.config;

import static codesquad.config.GlobalConfig.SESSION_TIMEOUT;

public class SessionConfig {

    private final long sessionTimeout;
    private final int sessionPoolMaxSize;

    public SessionConfig() {
        this.sessionTimeout = setSessionTimeout();
        this.sessionPoolMaxSize = setSessionPoolMaxSize();
    }

    public long getSessionTimeout() {
        return sessionTimeout;
    }

    public int getSessionPoolMaxSize() {
        return sessionPoolMaxSize;
    }

    protected long setSessionTimeout() {
        return SESSION_TIMEOUT;
    }

    protected int setSessionPoolMaxSize() {
        return 1000;
    }
}
