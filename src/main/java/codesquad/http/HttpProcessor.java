package codesquad.http;

import static codesquad.constants.ContentTypeConfig.UNKNOWN;
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

    private final HttpResponseFormatter httpResponseFormatter;

    public HttpProcessor(HttpResponseFormatter httpResponseFormatter) {
        this.httpResponseFormatter = httpResponseFormatter;
    }

    public String processRequest(HttpRequest httpRequest) throws IOException {
        String method = httpRequest.method();

        return switch (method) {
            case "GET" -> processGet(httpRequest);
            case "POST" -> processPost(httpRequest);
            default -> throw new IllegalArgumentException("Invalid method");
        };
    }

    private String processGet(HttpRequest httpRequest) throws IOException {
        String path = httpRequest.path();
        String staticFilePath = findStaticFilePath(path);

        Map<String, String> responseHeader = processHeader(httpRequest, staticFilePath);

        String staticFile = loadStaticFile(staticFilePath);

        HttpStatus status = HttpStatus.OK;

        HttpResponse httpResponse = new HttpResponse(httpRequest.version(),
                status,
                responseHeader,
                staticFile);

        return httpResponseFormatter.formatResponse(httpResponse);
    }

    private String processPost(HttpRequest httpRequest) {
        // todo implement post request

        return "";
    }

    private String loadStaticFile(String staticFilePath) throws IOException {
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

        ContentTypeConfig contentTypeConfig = ContentTypeConfig.fromFileExtension(fileExtension);

        String acceptHeaderValue = httpRequest.getHeader("Accept");
        if (acceptHeaderValue.contains(contentTypeConfig.getContentType())) {
            return contentTypeConfig.getContentType();
        }

        int firstAcceptValueIndex = acceptHeaderValue.indexOf(",");
        if (firstAcceptValueIndex != -1) {
            return acceptHeaderValue.substring(0, firstAcceptValueIndex);
        }

        return UNKNOWN.getContentType();
    }
}
