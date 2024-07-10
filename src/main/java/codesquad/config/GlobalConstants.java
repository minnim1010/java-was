package codesquad.config;

import java.util.Locale;

public class GlobalConstants {

    private static final GlobalConstants instance = new GlobalConstants();
    private final int serverPort;
    private final int requestThreadSize;
    private final String timezone;
    private final Locale locale;
    private final long sessionTimeout;
    private final int sessionPoolMaxSize;

    public GlobalConstants() {
        this.serverPort = setServerPort();
        this.requestThreadSize = setRequestThreadSize();
        this.timezone = setTimezone();
        this.locale = setLocale();
        this.sessionTimeout = setSessionTimeout();
        this.sessionPoolMaxSize = setSessionPoolMaxSize();
    }

    public static GlobalConstants getInstance() {
        return instance;
    }

    // ----------------------------------------------------- getter

    public int getServerPort() {
        return serverPort;
    }

    public int getRequestThreadSize() {
        return requestThreadSize;
    }

    public String getTimezone() {
        return timezone;
    }

    public Locale getLocale() {
        return locale;
    }

    public long getSessionTimeout() {
        return sessionTimeout;
    }

    public int getSessionPoolMaxSize() {
        return sessionPoolMaxSize;
    }

    // ----------------------------------------------------- setter

    protected int setServerPort() {
        return 8080;
    }

    protected int setRequestThreadSize() {
        return 10;
    }

    protected String setTimezone() {
        return "GMT";
    }

    protected Locale setLocale() {
        return Locale.US;
    }

    protected long setSessionTimeout() {
        return 1000 * 60 * 30;
    }

    protected int setSessionPoolMaxSize() {
        return 1000;
    }
}
