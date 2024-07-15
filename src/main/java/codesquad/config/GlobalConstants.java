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
    private final String datasourceUrl;
    private final String datasourceUser;
    private final String datasourcePassword;

    public GlobalConstants() {
        this.serverPort = setServerPort();
        this.requestThreadSize = setRequestThreadSize();
        this.timezone = setTimezone();
        this.locale = setLocale();
        this.sessionTimeout = setSessionTimeout();
        this.sessionPoolMaxSize = setSessionPoolMaxSize();
        this.datasourceUrl = setDatasourceUrl();
        this.datasourceUser = setDatasourceUser();
        this.datasourcePassword = setDatasourcePassword();
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

    public String getDatasourceUrl() {
        return datasourceUrl;
    }

    public String getDatasourceUser() {
        return datasourceUser;
    }

    public String getDatasourcePassword() {
        return datasourcePassword;
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

    protected String setDatasourceUrl() {
        return "jdbc:h2:/Users/woowatech22/Desktop/java-was/src/main/resources/data/was;AUTO_SERVER=TRUE";
    }

    protected String setDatasourceUser() {
        return "sa";
    }

    protected String setDatasourcePassword() {
        return "";
    }
}
