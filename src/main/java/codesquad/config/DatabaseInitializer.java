package codesquad.config;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseInitializer {

    private static final Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);

    private DatabaseInitializer() {
    }

    public static void initializeDatabase() {
        WasConfiguration wasConfiguration = WasConfiguration.getInstance();
        String datasourceUrl = wasConfiguration.getDatasourceUrl();
        int startIndex = datasourceUrl.lastIndexOf(":") + 1;
        int endIndex = datasourceUrl.indexOf(";");

        String datasourcePath = null;
        if (startIndex > 0 && endIndex > startIndex) {
            datasourcePath = datasourceUrl.substring(startIndex, endIndex);
        }
        if (datasourcePath == null) {
            log.error("Failed to extract database file path from datasource url.");
            System.exit(-1);
        }

        File dbFile = new File(datasourcePath + ".mv.db");
        if (!dbFile.exists()) {
            log.info("Database file not found. Initializing database...");

            try (Connection conn = DriverManager.getConnection(wasConfiguration.getDatasourceUrl(),
                    wasConfiguration.getDatasourceUser(), wasConfiguration.getDatasourcePassword());
                 Statement stmt = conn.createStatement()) {

                InputStream inputStream = DatabaseInitializer.class.getResourceAsStream("/init.sql");
                if (inputStream == null) {
                    log.error("init.sql file not found in resources.");
                    return;
                }

                Scanner scanner = new Scanner(inputStream, "UTF-8");
                StringBuilder sql = new StringBuilder();
                while (scanner.hasNextLine()) {
                    sql.append(scanner.nextLine()).append("\n");
                }
                scanner.close();

                stmt.execute(sql.toString());
                log.info("Database initialized successfully.");

            } catch (SQLException e) {
                log.error("Failed to initialize database.", e);
            }
        } else {
            log.info("Database file exists. No initialization needed.");
        }
    }
}
