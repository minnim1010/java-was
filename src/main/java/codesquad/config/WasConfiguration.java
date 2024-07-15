package codesquad.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WasConfiguration {

    private static final WasConfiguration instance = new WasConfiguration();
    private static final Logger log = LoggerFactory.getLogger(WasConfiguration.class);
    private static final String CONFIGURATION_FILE = "setting.properties";

    private static Properties properties;
    private final int serverPort;
    private final int requestThreadSize;
    private final String timezone;
    private final Locale locale;
    private final long sessionTimeout;
    private final int sessionPoolMaxSize;
    private final String datasourceUrl;
    private final String datasourceUser;
    private final String datasourcePassword;

    private WasConfiguration() {
        properties = new Properties();
        loadProperties();

        this.serverPort = Integer.parseInt(this.getStringProperty("server.port", "8080"));
        this.requestThreadSize = Integer.parseInt(this.getStringProperty("server.request.thread.size", "10"));
        this.timezone = this.getStringProperty("server.timezone", "GMT");
        this.locale = new Locale(this.getStringProperty("server.locale.language", "en"),
                this.getStringProperty("server.locale.country", "US"));
        this.sessionTimeout = Long.parseLong(this.getStringProperty("server.session.timeout", "1800000"));
        this.sessionPoolMaxSize = Integer.parseInt(this.getStringProperty("server.session.pool.max.size", "1000"));
        this.datasourceUrl = this.getStringProperty("datasource.url",
                "jdbc:h2:/Users/woowatech22/Desktop/java-was/src/main/resources/data/was;AUTO_SERVER=TRUE");
        this.datasourceUser = this.getStringProperty("datasource.user", "sa");
        this.datasourcePassword = this.getStringProperty("datasource.password", "");
    }

    public static WasConfiguration getInstance() {
        return instance;
    }

    private static void loadProperties() {
        InputStream in = null;
        try {
            ClassLoader threadCL = Thread.currentThread().getContextClassLoader();
            if (threadCL != null) {
                in = threadCL.getResourceAsStream(CONFIGURATION_FILE);
            }
            if (in == null) {
                in = ClassLoader.getSystemResourceAsStream(CONFIGURATION_FILE);
            }
            if (in != null) {
                properties.load(in);
            } else {
                throw new IOException("Property file 'setting.properties' not found in the classpath");
            }
        } catch (IOException e) {
            log.error("IOException: ", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error("IOException while closing stream", e);
                }
            }
        }
    }

    String getStringProperty(String name, String defaultValue) {
        String prop = this.getStringProperty(name);
        return prop == null ? defaultValue : prop;
    }

    String getStringProperty(String name) {
        String prop = System.getProperty(name);
        return prop == null ? properties.getProperty(name) : prop;
    }

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
}
