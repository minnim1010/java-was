package codesquad.environment;

import codesquad.config.WasConfiguration;
import codesquad.http.session.SessionIdGenerator;
import codesquad.http.session.SessionManager;
import codesquad.template.NodeProcessor;
import codesquad.template.TemplateEngine;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TestEnvironment {

    private static final Logger log = LoggerFactory.getLogger(TestEnvironment.class);

    protected static SessionManager sessionManager = SessionManager.createInstance(
            10,
            100000,
            new SessionIdGenerator() {

                @Override
                public String generate() {
                    return "session-id";
                }
            });

    protected static TemplateEngine templateEngine = TemplateEngine.createInstance(new NodeProcessor());

    @BeforeAll
    protected static void init() {

    }

    private static void executeSqlFile(Connection connection, String filePath) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(getInputStream(filePath)))) {
            StringBuilder sql = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sql.append(line);
                sql.append(System.lineSeparator());
            }
            executeSqlStatements(connection, sql.toString());
        } catch (IOException e) {
            log.error("IOException: ", e);
        }
    }

    private static InputStream getInputStream(String fileName) throws IOException {
        InputStream in = null;
        ClassLoader threadCL = Thread.currentThread().getContextClassLoader();
        if (threadCL != null) {
            in = threadCL.getResourceAsStream(fileName);
        }
        if (in == null) {
            in = ClassLoader.getSystemResourceAsStream(fileName);
        }
        if (in != null) {
            return in;
        } else {
            throw new IOException("Property file 'setting.yml' not found in the classpath");
        }
    }

    private static void executeSqlStatements(Connection connection, String sql) {
        String[] sqlStatements = sql.split(";");
        try (Statement statement = connection.createStatement()) {
            for (String sqlStatement : sqlStatements) {
                sqlStatement = sqlStatement.trim();
                if (!sqlStatement.isEmpty()) {
                    statement.execute(sqlStatement);
                }
            }
        } catch (SQLException e) {
            log.error("SQLException: ", e);
        }
    }

    @BeforeEach
    protected void setUpTestEnvironment() throws Exception {
        WasConfiguration instance = WasConfiguration.getInstance();
        setField(instance, "serverPort", 9090);
        setField(instance, "requestThreadSize", 10);
        setField(instance, "sessionTimeout", 3600000L);
        setField(instance, "sessionPoolMaxSize", 2000);
        setField(instance, "datasourceUrl",
                "jdbc:h2:/Users/woowatech22/Desktop/java-was/src/main/resources/data/test_was;AUTO_SERVER=TRUE");
        setField(instance, "datasourceUser", "test");
        setField(instance, "datasourcePassword", "test");

        sessionManager.clear();

        String jdbcUrl = "jdbc:h2:/Users/woowatech22/Desktop/java-was/src/main/resources/data/test_was;AUTO_SERVER=TRUE";
        String username = "test";
        String password = "test";
        String sqlFilePath = "init.sql";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            executeSqlFile(connection, sqlFilePath);
        } catch (SQLException e) {
            log.error("SQLException: ", e);
        }
    }

    private void setField(Object instance, String fieldName, Object value) throws Exception {
        Field field = WasConfiguration.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(instance, value);
    }
}
