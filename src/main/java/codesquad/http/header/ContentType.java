package codesquad.http.header;

public enum ContentType {
    HTML("html", "text/html"),
    HTM("htm", "text/html"),
    CSS("css", "text/css"),
    JS("js", "application/javascript"),
    JSON("json", "application/json"),
    XML("xml", "application/xml"),
    TXT("txt", "text/plain"),
    PNG("png", "image/png"),
    JPG("jpg", "image/jpeg"),
    JPEG("jpeg", "image/jpeg"),
    GIF("gif", "image/gif"),
    SVG("svg", "image/svg+xml"),
    ICO("ico", "image/x-icon"),
    PDF("pdf", "application/pdf"),
    ZIP("zip", "application/zip"),
    DOC("doc", "application/msword"),
    DOCX("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    XLS("xls", "application/vnd.ms-excel"),
    XLSX("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    PPT("ppt", "application/vnd.ms-powerpoint"),
    PPTX("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"),
    UNKNOWN("", "application/octet-stream");

    private final String fileExtension;
    private final String contentType;

    ContentType(String fileExtension, String contentType) {
        this.fileExtension = fileExtension;
        this.contentType = contentType;
    }

    public static String getContentTypeByExtension(String fileExtension) {
        for (ContentType config : values()) {
            if (config.fileExtension.equalsIgnoreCase(fileExtension)) {
                return config.contentType;
            }
        }
        return UNKNOWN.contentType;
    }

    public static ContentType fromFileExtension(String fileExtension) {
        for (ContentType config : values()) {
            if (config.fileExtension.equalsIgnoreCase(fileExtension)) {
                return config;
            }
        }
        return UNKNOWN;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public String getContentType() {
        return contentType;
    }
}
