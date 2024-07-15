package codesquad.utils;

import codesquad.config.GlobalConstants;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionUtils {

    private DatabaseConnectionUtils() {
    }

    public static Connection getConnection() throws SQLException {
        String url = GlobalConstants.getInstance().getDatasourceUrl();
        String user = GlobalConstants.getInstance().getDatasourceUser();
        String password = GlobalConstants.getInstance().getDatasourcePassword();

        return DriverManager.getConnection(url, user, password);
    }
}
