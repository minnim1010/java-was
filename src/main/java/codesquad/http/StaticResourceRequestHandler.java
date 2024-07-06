package codesquad.http;

import static codesquad.http.header.HeaderField.ACCEPT;
import static codesquad.http.header.HeaderField.CONTENT_TYPE;
import static codesquad.utils.FileUtils.getFileExtension;

import codesquad.config.GlobalConfig;
import codesquad.error.ResourceNotFoundException;
import codesquad.error.UnSupportedMediaTypeException;
import codesquad.http.header.ContentType;
import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;
import codesquad.http.property.HttpStatus;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Set;

public class StaticResourceRequestHandler {

    private final Set<String> staticResourcePaths;

    public StaticResourceRequestHandler(Set<String> staticResourcePaths) {
        this.staticResourcePaths = staticResourcePaths;
    }

    public void handle(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        String path = httpRequest.getUri().getPath();
        String staticResourcePath = findStaticResourcePath(path);
        URL fileUrl = getClass().getClassLoader().getResource("static" + staticResourcePath);
        byte[] fileContent = loadFile(fileUrl);

        httpResponse.setBody(fileContent);
        httpResponse.setHeader(CONTENT_TYPE.getFieldName(), determineContentType(httpRequest, staticResourcePath));

        httpResponse.setStatus(HttpStatus.OK);
    }

    private String findStaticResourcePath(String path) {
        if (staticResourcePaths.contains(path)) {
            return path;
        }

        return GlobalConfig.DEFAULT_PAGES.stream()
                .map(defaultPage -> path + (path.endsWith("/") ? "" : "/") + defaultPage)
                .filter(staticResourcePaths::contains)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found: " + path));
    }

    private byte[] loadFile(URL fileUrl) throws IOException {
        try (InputStream inputStream = fileUrl.openStream()) {
            return inputStream.readAllBytes();
        }
    }

    private String determineContentType(HttpRequest httpRequest, String staticFilePath) {
        String fileExtension = getFileExtension(staticFilePath);
        if (fileExtension.isEmpty()) {
            return ContentType.UNKNOWN.getContentType();
        }

        String contentType = ContentType.getContentTypeByExtension(fileExtension);

        String acceptHeaderValue = httpRequest.getHeader(ACCEPT.getFieldName());
        if (acceptHeaderValue.contains(contentType) || acceptHeaderValue.contains("*/*")) {
            return contentType;
        }

        throw new UnSupportedMediaTypeException("Unsupported Media Type " + fileExtension);
    }
}
