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
import codesquad.http.property.HttpMethod;
import codesquad.http.property.HttpStatus;
import codesquad.utils.FileUtils;
import java.io.IOException;

public class HttpRequestProcessor {

    private static final RequestHandlerResolver REQUEST_HANDLER_RESOLVER = new RequestHandlerResolver();

    public void processRequest(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        HttpMethod method = httpRequest.getMethod();
        String path = httpRequest.getUri().getPath();

        RequestHandler requestHandler = REQUEST_HANDLER_RESOLVER.resolve(path);
        if (requestHandler == null) {
            responseStaticFile(httpRequest, httpResponse);
            return;
        }

        switch (method) {
            case GET -> requestHandler.processGet(httpRequest, httpResponse);
            case POST -> requestHandler.processPost(httpRequest, httpResponse);
            default -> throw new UnsupportedOperationException("Unsupported HTTP Method " + method);
        }
    }

    private void responseStaticFile(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        String path = httpRequest.getUri().getPath();
        String filePath = findFilePath(path);
        byte[] fileContent = loadFile(filePath);
        httpResponse.setHeader(CONTENT_TYPE.getFieldName(), processAcceptHeader(httpRequest, filePath));
        httpResponse.setBody(fileContent);

        httpResponse.setStatus(HttpStatus.OK);
        httpResponse.setVersion(httpRequest.getVersion());
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