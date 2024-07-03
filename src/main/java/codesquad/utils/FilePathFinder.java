package codesquad.utils;

public final class FilePathFinder {

    private static final String projectPath = System.getProperty("user.dir");

    private FilePathFinder() {
    }

    public static String findStaticFilePath(String fileName) {
        return projectPath + "/src/main/resources/static" + fileName;
    }
}
