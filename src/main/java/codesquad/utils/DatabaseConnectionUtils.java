package codesquad.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionUtils {

    private DatabaseConnectionUtils() {
    }

    public static Connection getConnection() throws SQLException {
        //get connection db
        String url = "jdbc:h2:/Users/woowatech22/Desktop/java-was/src/main/resources/data/was;AUTO_SERVER=TRUE";
        String user = "sa";
        String password = "";

        return DriverManager.getConnection(url, user, password);
    }
}
