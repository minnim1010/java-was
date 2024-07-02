package codesquad.http;

import static codesquad.utils.FileUtils.findStaticFilePath;
import static codesquad.utils.FileUtils.getFileExtension;

import codesquad.constants.ContentTypeConfig;
import codesquad.http.property.HttpStatus;
import java.io.BufferedReader;
import java.io.FileReader;
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

        String staticFile = loadFile(staticFilePath);

        HttpStatus status = HttpStatus.OK;

        return new HttpResponse(httpRequest.version(),
                status,
                responseHeader,
                staticFile);
    }

    private HttpResponse processPost(HttpRequest httpRequest) {
        // todo implement post request

        return null;
    }

    private String loadFile(String staticFilePath) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(staticFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
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
        if (acceptHeaderValue.contains(contentType)) {
            return contentType;
        }

        int firstAcceptValueIndex = acceptHeaderValue.indexOf(",");
        if (firstAcceptValueIndex != -1) {
            return acceptHeaderValue.substring(0, firstAcceptValueIndex);
        }

        return ContentTypeConfig.UNKNOWN.getContentType();
    }
}
