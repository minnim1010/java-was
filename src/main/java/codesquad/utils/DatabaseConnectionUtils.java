package codesquad.utils;

import codesquad.config.WasConfiguration;
import java.sql.Connection;
import java.sql.SQLException;
import org.h2.jdbcx.JdbcConnectionPool;

public class DatabaseConnectionUtils {

    private static final JdbcConnectionPool jdbcConnectionPool;

    static {
        WasConfiguration wasConfiguration = WasConfiguration.getInstance();
        String url = wasConfiguration.getDatasourceUrl();
        String user = wasConfiguration.getDatasourceUser();
        String password = wasConfiguration.getDatasourcePassword();

        jdbcConnectionPool = JdbcConnectionPool.create(url, user, password);
    }

    private DatabaseConnectionUtils() {
    }

    public static Connection getConnection() throws SQLException {
        return jdbcConnectionPool.getConnection();
    }
}
