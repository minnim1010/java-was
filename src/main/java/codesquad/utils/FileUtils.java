package codesquad.utils;

import codesquad.http.error.ResourceNotFoundException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public final class FileUtils {

    private static final String PROJECT_PATH = System.getProperty("user.dir");

    private FileUtils() {
    }

    public static String findAbsoluteFilePath(String fileName) {
        return PROJECT_PATH + "/src/main/resources/static" + fileName;
    }

    public static String getFileExtension(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "";
        }
        return fileName.substring(lastIndexOf + 1);
    }

    public static boolean isExists(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }

    public static byte[] loadFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || file.isDirectory()) {
            throw new ResourceNotFoundException("Resource not found" + filePath);
        }

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] fileBytes = new byte[(int) file.length()];
            fileInputStream.read(fileBytes);
            return fileBytes;
        }
    }
}
