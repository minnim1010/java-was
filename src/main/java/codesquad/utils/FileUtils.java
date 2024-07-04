package codesquad.utils;

public final class FileUtils {

    private static final String PROJECT_PATH = System.getProperty("user.dir");

    private FileUtils() {
    }

    public static String findStaticFilePath(String fileName) {
        return PROJECT_PATH + "/src/main/resources/static" + fileName;
    }

    public static String getFileExtension(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "";
        }
        return fileName.substring(lastIndexOf + 1);
    }
}
