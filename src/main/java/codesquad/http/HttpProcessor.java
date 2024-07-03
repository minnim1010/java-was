package codesquad.http;

import static codesquad.utils.FileUtils.findStaticFilePath;
import static codesquad.utils.FileUtils.getFileExtension;

import codesquad.constants.ContentTypeConfig;
import codesquad.error.ResourceNotFoundException;
import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;
import codesquad.http.property.HttpStatus;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpProcessor {

    public HttpResponse processRequest(HttpRequest httpRequest) throws IOException {
        return switch (httpRequest.method()) {
            case GET -> processGet(httpRequest);
            case POST -> processPost(httpRequest);
        };
    }

    private HttpResponse processGet(HttpRequest httpRequest) throws IOException {
        String path = httpRequest.path();
        String staticFilePath = findStaticFilePath(path);

        Map<String, String> responseHeader = processHeader(httpRequest, staticFilePath);

        byte[] fileContent = loadFile(staticFilePath);

        HttpStatus status = HttpStatus.OK;

        return new HttpResponse(httpRequest.version(),
                status,
                responseHeader,
                fileContent);
    }

    private HttpResponse processPost(HttpRequest httpRequest) {
        // todo implement post request

        return null;
    }

    private byte[] loadFile(String staticFilePath) throws IOException {
        File file = new File(staticFilePath);
        if (!file.exists()) {
            throw new ResourceNotFoundException("File not found" + staticFilePath);
        }

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] fileBytes = new byte[(int) file.length()];
            fileInputStream.read(fileBytes);
            return fileBytes;
        }
    }

    private Map<String, String> processHeader(HttpRequest httpRequest, String staticFilePath) {
        Map<String, String> responseHeader = new HashMap<>();
        responseHeader.put("Content-Type", processAcceptHeader(httpRequest, staticFilePath));
        return responseHeader;
    }

    private String processAcceptHeader(HttpRequest httpRequest, String staticFilePath) {
        String fileExtension = getFileExtension(staticFilePath);
        String contentType = ContentTypeConfig.getContentTypeByExtension(fileExtension);

        String acceptHeaderValue = httpRequest.getHeader("Accept");
        if (acceptHeaderValue.contains(contentType) || acceptHeaderValue.contains("*/*")) {
            return contentType;
        }

        int firstAcceptValueIndex = acceptHeaderValue.indexOf(",");
        if (firstAcceptValueIndex != -1) {
            return acceptHeaderValue.substring(0, firstAcceptValueIndex);
        }

        return ContentTypeConfig.UNKNOWN.getContentType();
        //todo 없으면 406 Not Acceptable
    }
}
