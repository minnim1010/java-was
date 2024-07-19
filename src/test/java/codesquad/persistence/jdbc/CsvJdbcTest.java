package codesquad.persistence.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("CSV JDBC 테스트")
class CsvJdbcTest {

    private static final String CSV_DIRECTORY = "src/test/resources/db";
    private static final String MEMBER_CSV_PATH = CSV_DIRECTORY + "/MEMBER.csv";

    @BeforeEach
    void setup() throws IOException {
        File csvDir = new File(CSV_DIRECTORY);
        if (!csvDir.exists()) {
            csvDir.mkdirs();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(MEMBER_CSV_PATH))) {
            writer.write("userId,password,name,email");
            writer.newLine();
            writer.write(CsvUtils.encode("A") + "," + CsvUtils.encode("a") + "," + CsvUtils.encode("a") + ","
                    + CsvUtils.encode("a"));
            writer.newLine();
            writer.write(CsvUtils.encode("B") + "," + CsvUtils.encode("b") + "," + CsvUtils.encode("b") + ","
                    + CsvUtils.encode("b"));
            writer.newLine();
            writer.write(CsvUtils.encode("C") + "," + CsvUtils.encode("c") + "," + CsvUtils.encode("c") + ","
                    + CsvUtils.encode("c"));
            writer.newLine();
        }
    }

    @AfterEach
    void cleanup() {
        File csvFile = new File(MEMBER_CSV_PATH);
        if (csvFile.exists()) {
            csvFile.delete();
        }
        File csvDir = new File(CSV_DIRECTORY);
        if (csvDir.exists()) {
            csvDir.delete();
        }
    }

    @Test
    void testCreateTable() {
        try (Connection conn = DriverManager.getConnection("jdbc:csv:" + CSV_DIRECTORY);
             Statement stmt = conn.createStatement()) {

            String createTableSql = "CREATE TABLE TEST_TABLE " +
                    "(userId VARCHAR(255) PRIMARY KEY, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "email VARCHAR(255) NOT NULL)";
            stmt.execute(createTableSql);

            File createdFile = new File(CSV_DIRECTORY + "/TEST_TABLE.csv");
            assertTrue(createdFile.exists());

            List<String> lines = java.nio.file.Files.readAllLines(createdFile.toPath());
            assertEquals(1, lines.size());
            assertEquals("userId,password,name,email", lines.get(0));

            createdFile.delete();
        } catch (SQLException | IOException e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    void testSelectAllFromMember() {
        try (Connection conn = DriverManager.getConnection("jdbc:csv:" + CSV_DIRECTORY);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM MEMBER");
             ResultSet rs = stmt.executeQuery()) {

            assertTrue(rs.next());
            assertEquals("A", rs.getString("userId"));
            assertEquals("a", rs.getString("password"));
            assertEquals("a", rs.getString("name"));
            assertEquals("a", rs.getString("email"));

            assertTrue(rs.next());
            assertEquals("B", rs.getString("userId"));
            assertEquals("b", rs.getString("password"));
            assertEquals("b", rs.getString("name"));
            assertEquals("b", rs.getString("email"));

            assertTrue(rs.next());
            assertEquals("C", rs.getString("userId"));
            assertEquals("c", rs.getString("password"));
            assertEquals("c", rs.getString("name"));
            assertEquals("c", rs.getString("email"));

            assertFalse(rs.next());
        } catch (SQLException e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    void testSelectMemberByUserId() {
        try (Connection conn = DriverManager.getConnection("jdbc:csv:" + CSV_DIRECTORY);
             PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM MEMBER WHERE userId = ?")) {

            preparedStatement.setString(1, "A");
            ResultSet rs = preparedStatement.executeQuery();

            assertTrue(rs.next());
            assertEquals("A", rs.getString("userId"));
            assertEquals("a", rs.getString("password"));
            assertEquals("a", rs.getString("name"));
            assertEquals("a", rs.getString("email"));

            assertFalse(rs.next());
        } catch (SQLException e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    void testInsertMember() {
        try (Connection conn = DriverManager.getConnection("jdbc:csv:" + CSV_DIRECTORY);
             PreparedStatement preparedStatement = conn.prepareStatement(
                     "INSERT INTO MEMBER (userId, password, name, email) VALUES (?, ?, ?, ?)")) {

            preparedStatement.setString(1, "D");
            preparedStatement.setString(2, "d");
            preparedStatement.setString(3, "David");
            preparedStatement.setString(4, "david@example.com");

            int rowsInserted = preparedStatement.executeUpdate();
            assertEquals(1, rowsInserted);

            try (PreparedStatement selectStatement = conn.prepareStatement("SELECT * FROM MEMBER WHERE userId = ?")) {
                selectStatement.setString(1, "D");
                ResultSet rs = selectStatement.executeQuery();

                assertTrue(rs.next());
                assertEquals("D", rs.getString("userId"));
                assertEquals("d", rs.getString("password"));
                assertEquals("David", rs.getString("name"));
                assertEquals("david@example.com", rs.getString("email"));

                assertFalse(rs.next());
            }
        } catch (SQLException e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    void testDeleteAllMembers() {
        try (Connection conn = DriverManager.getConnection("jdbc:csv:" + CSV_DIRECTORY);
             PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM MEMBER")) {

            int rowsDeleted = preparedStatement.executeUpdate();
            assertEquals(3, rowsDeleted);
        } catch (SQLException e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }
}