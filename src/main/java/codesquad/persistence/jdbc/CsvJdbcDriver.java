package codesquad.persistence.jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

public class CsvJdbcDriver implements Driver {

    static {
        try {
            DriverManager.registerDriver(new CsvJdbcDriver());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to register driver");
        }
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        if (!url.startsWith("jdbc:csv:")) {
            return null;
        }
        String path = url.substring("jdbc:csv:".length());
        return new CsvConnection(path);
    }

    @Override
    public boolean acceptsURL(String url) {
        return url.startsWith("jdbc:csv:");
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return new DriverPropertyInfo[0];
    }

    @Override
    public int getMajorVersion() {
        return 0;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public boolean jdbcCompliant() {
        return false;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}
