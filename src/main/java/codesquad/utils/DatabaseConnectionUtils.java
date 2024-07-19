package codesquad.utils;

import codesquad.config.WasConfiguration;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionUtils {

    private DatabaseConnectionUtils() {
    }

    public static Connection getConnection() throws SQLException {
        WasConfiguration wasConfiguration = WasConfiguration.getInstance();
        String url = wasConfiguration.getDatasourceUrl();
        String user = wasConfiguration.getDatasourceUser();
        String password = wasConfiguration.getDatasourcePassword();

        return DriverManager.getConnection(url, user, password);
    }
}
