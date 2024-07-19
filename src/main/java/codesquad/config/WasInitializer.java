package codesquad.config;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WasInitializer {

    private static final Logger log = LoggerFactory.getLogger(WasInitializer.class);

    private WasInitializer() {
    }

    public static void init() {
        initializeImagePath();
        initializeDatabase();
    }

    public static void initializeDatabase() {
        WasConfiguration wasConfiguration = WasConfiguration.getInstance();
        String datasourceUrl = wasConfiguration.getDatasourceUrl();

        int startIndex = datasourceUrl.lastIndexOf(":") + 1;
        int endIndex = datasourceUrl.indexOf(";");
        if (endIndex == -1) {
            endIndex = datasourceUrl.length();
        }

        String datasourcePath = null;
        if (startIndex > 0 && endIndex > startIndex) {
            datasourcePath = datasourceUrl.substring(startIndex, endIndex);
        }
        if (datasourcePath == null) {
            log.error("Failed to extract database file path from datasource url.");
            System.exit(-1);
        }

        File dbFile = new File(datasourcePath);
        if (!dbFile.exists()) {
            dbFile.mkdirs();

            log.info("Database file not found. Initializing database...");

            try (Connection conn = DriverManager.getConnection(datasourceUrl);
                 Statement stmt = conn.createStatement()) {

                List<String> init = new ArrayList<>();
                init.add("CREATE TABLE MEMBER " +
                        "(userId VARCHAR(255) PRIMARY KEY, " +
                        "password VARCHAR(255) NOT NULL, " +
                        "name VARCHAR(255) NOT NULL, " +
                        "email VARCHAR(255) NOT NULL)");
                init.add("CREATE TABLE ARTICLE " +
                        "(articleId VARCHAR(255) PRIMARY KEY, " +
                        "title VARCHAR(255) NOT NULL, " +
                        "content TEXT NOT NULL, " +
                        "imagePath VARCHAR(255) NOT NULL, " +
                        "createdAt TIMESTAMP NOT NULL, " +
                        "userId VARCHAR(255) NOT NULL)");
                init.add("CREATE TABLE COMMENT " +
                        "(commentId VARCHAR(255) PRIMARY KEY, " +
                        "content TEXT NOT NULL, " +
                        "createdAt TIMESTAMP NOT NULL, " +
                        "userId VARCHAR(255) NOT NULL, " +
                        "articleId VARCHAR(255) NOT NULL)");

                for (String sql : init) {
                    stmt.execute(sql);
                }
                log.info("Database initialized successfully.");

            } catch (SQLException e) {
                log.error("Failed to initialize database.", e);
            }
        } else {
            log.info("Database file exists. No initialization needed.");
        }
    }

    public static void initializeImagePath() {
        WasConfiguration wasConfiguration = WasConfiguration.getInstance();
        File imageDir = new File(wasConfiguration.getImagePath());
        if (!imageDir.exists()) {
            imageDir.mkdirs();
            log.info("Image directory created: " + imageDir.getAbsolutePath());
        }
    }
}
