package codesquad.utils;

import codesquad.config.WasConfiguration;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionUtils {

    private DatabaseConnectionUtils() {
    }

    public static Connection getConnection() throws SQLException {
        String url = WasConfiguration.getInstance().getDatasourceUrl();
        String user = WasConfiguration.getInstance().getDatasourceUser();
        String password = WasConfiguration.getInstance().getDatasourcePassword();

        return DriverManager.getConnection(url, user, password);
    }
}
