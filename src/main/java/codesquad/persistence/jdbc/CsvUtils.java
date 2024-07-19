package codesquad.persistence.jdbc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class CsvUtils {

    private CsvUtils() {
    }

    public static void createCsvFile(String filePath, String header) throws SQLException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(header);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw new SQLException("Failed to create CSV file", e);
        }
    }

    public static List<String[]> readCsv(String path) throws SQLException {
        List<String[]> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                data.add(line.split(","));
            }
        } catch (IOException e) {
            throw new SQLException("Failed to read CSV file", e);
        }
        return data;
    }

    public static void appendCsvFile(String filePath, String string) throws SQLException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(string);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw new SQLException(e);
        }
    }

    public static void writeCsvFile(String filePath, List<String[]> newData) throws SQLException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String[] data : newData) {
                writer.write(String.join(",", data));
                writer.newLine();
                writer.flush();
            }
        } catch (IOException e) {
            throw new SQLException(e);
        }
    }

    public static String encode(String data) {
        return Base64.getEncoder().encodeToString(data.getBytes());
    }

    public static String decode(String data) {
        return new String(Base64.getDecoder().decode(data));
    }
}
