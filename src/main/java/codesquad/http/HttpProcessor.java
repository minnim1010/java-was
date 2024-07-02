package codesquad.http;

import static codesquad.utils.FilePathFinder.findStaticFilePath;

import codesquad.http.property.HttpStatus;
import codesquad.http.property.HttpVersion;
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

    public String processRequest(MyHttpRequest httpRequest) throws IOException {
        String method = httpRequest.method();

        return switch (method) {
            case "GET" -> processGetRequest(httpRequest);
            case "POST" -> processPostRequest(httpRequest);
            default -> throw new IllegalArgumentException("Invalid method");
        };
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

    private String processPostRequest(MyHttpRequest httpRequest) {
        // todo implement post request

        return "";
    }

    private String processGetRequest(MyHttpRequest httpRequest) throws IOException {
        String path = httpRequest.path();
        String staticFilePath = findStaticFilePath(path);

        Map<String, String> responseHeader = processHeader(httpRequest, staticFilePath);
        String staticFile = loadStaticFile(staticFilePath);
        HttpStatus status = HttpStatus.OK;

        MyHttpResponse myHttpResponse = new MyHttpResponse(HttpVersion.HTTP_1_1,
                status,
                responseHeader,
                staticFile);

        return httpResponseFormatter.formatResponse(myHttpResponse);
    }

    private Map<String, String> processHeader(MyHttpRequest httpRequest, String staticFilePath) {
        Map<String, String> responseHeader = new HashMap<>();
        responseHeader.put("Content-Type", processAcceptHeader(httpRequest, staticFilePath));
        return responseHeader;
    }

    private String processAcceptHeader(MyHttpRequest httpRequest, String staticFilePath) {
        String acceptHeaderValue = httpRequest.getHeader("Accept");

        if (staticFilePath.endsWith(".svg")) {
            return "image/svg+xml";
        }
        if (staticFilePath.endsWith(".ico")) {
            return "image/png";
        }

        int firstAcceptValueIndex = acceptHeaderValue.indexOf(",");
        if (firstAcceptValueIndex != -1) {
            return acceptHeaderValue.substring(0, firstAcceptValueIndex);
        }

        return "text/plain";
    }
}
