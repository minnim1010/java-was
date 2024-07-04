package codesquad.http;

import static codesquad.http.header.HeaderField.ACCEPT;
import static codesquad.http.header.HeaderField.CONTENT_TYPE;
import static codesquad.utils.FileUtils.findAbsoluteFilePath;
import static codesquad.utils.FileUtils.getFileExtension;

import codesquad.config.GlobalConfig;
import codesquad.error.ResourceNotFoundException;
import codesquad.error.UnSupportedMediaTypeException;
import codesquad.http.header.ContentType;
import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;
import codesquad.http.property.HttpStatus;
import codesquad.utils.FileUtils;
import java.io.IOException;

public class HttpRequestProcessor {

    public void processRequest(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        switch (httpRequest.method()) {
            case GET -> processGet(httpRequest, httpResponse);
            case POST -> processPost(httpRequest, httpResponse);
            default -> throw new UnsupportedOperationException("Unsupported HTTP Method " + httpRequest.method());
        }
    }

    private void processGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        String path = httpRequest.path();
        String filePath = findFilePath(path);

        httpResponse.setHeader(CONTENT_TYPE.getFieldName(), processAcceptHeader(httpRequest, filePath));
        byte[] fileContent = loadFile(filePath);
        httpResponse.setBody(fileContent);

        httpResponse.setStatus(HttpStatus.OK);
        httpResponse.setVersion(httpRequest.version());
    }

    private String findFilePath(String path) {
        String absoluteFilePath = findAbsoluteFilePath(path);

        if (FileUtils.isExists(absoluteFilePath)) {
            return absoluteFilePath;
        }

        return GlobalConfig.DEFAULT_PAGES.stream()
                .map(defaultPage -> absoluteFilePath + "/" + defaultPage)
                .filter(FileUtils::isExists)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found" + absoluteFilePath));
    }

    private byte[] loadFile(String staticFilePath) throws IOException {
        if (FileUtils.isExists(staticFilePath)) {
            return FileUtils.loadFile(staticFilePath);
        }

        String alterFilePath = GlobalConfig.DEFAULT_PAGES.stream()
                .map(defaultPage -> staticFilePath + "/" + defaultPage)
                .filter(FileUtils::isExists)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found" + staticFilePath));

        return FileUtils.loadFile(alterFilePath);
    }


    private void processPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        // todo implement post request
    }

    private String processAcceptHeader(HttpRequest httpRequest, String staticFilePath) {
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