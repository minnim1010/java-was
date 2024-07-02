package codesquad.http;

import static codesquad.utils.FilePathFinder.findStaticFilePath;

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

    private static char[] loadStaticFile(String staticFilePath) throws IOException {
        char[] buffer = new char[500000];
        try (BufferedReader br = new BufferedReader(new FileReader(staticFilePath))) {
            int length = br.read(buffer);
            if (length == -1) {
                throw new IOException("File not found");
            }
        }
        return buffer;
    }

    public String processRequest(MyHttpRequest httpRequest) throws IOException {
        String method = httpRequest.method();

        return switch (method) {
            case "GET" -> processGetRequest(httpRequest);
            case "POST" -> processPostRequest(httpRequest);
            default -> throw new IllegalArgumentException("Invalid method");
        };
    }

    private String processPostRequest(MyHttpRequest httpRequest) {
        return "";
    }

    private String processGetRequest(MyHttpRequest httpRequest) throws IOException {
        String path = httpRequest.path();
        String staticFilePath = findStaticFilePath(path);

        Map<String, String> responseHeader = processHeader(httpRequest, staticFilePath);
        char[] buffer = loadStaticFile(staticFilePath);
        HttpStatus status = HttpStatus.OK;

        MyHttpResponse myHttpResponse = new MyHttpResponse(HttpVersion.HTTP_1_1,
                status,
                responseHeader,
                new String(buffer));

        return httpResponseFormatter.formatResponse(myHttpResponse);
    }

    private Map<String, String> processHeader(MyHttpRequest httpRequest, String staticFilePath) {
        Map<String, String> responseHeader = new HashMap<>();
        responseHeader.put("Content-Type", processAcceptHeader(httpRequest, staticFilePath));
//        responseHeader.put("Content-Encoding", processEncodingHeader(httpRequest));
        return responseHeader;
    }

    private String processAcceptHeader(MyHttpRequest httpRequest, String staticFilePath) {
        String acceptHeaderValue = httpRequest.getHeader("Accept");

        if (staticFilePath.endsWith(".svg") || staticFilePath.endsWith(".ico")) {
            return "image/svg+xml";
        }

        int firstAcceptValueIndex = acceptHeaderValue.indexOf(",");
        if (firstAcceptValueIndex != -1) {
            return acceptHeaderValue.substring(0, firstAcceptValueIndex);
        }

        return "text/plain";
    }

    private String processEncodingHeader(MyHttpRequest httpRequest) {
        String path = httpRequest.path();
        if (path.endsWith(".svg") || path.endsWith(".ico")) {
            return "gzip";
        }
        return "";
    }
}
