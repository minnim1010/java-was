package codesquad.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpParser {

    private static final Logger logger = LoggerFactory.getLogger(HttpParser.class);

    public Optional<MyHttpRequest> parse(String httpRequestStr) {
        try{
            String[] lines = httpRequestStr.split("\n");

            String[] requestLine = lines[0].split(" ");
            String method = parseMethod(requestLine);
            String path = requestLine[1];

            Map<String, String> headers = new HashMap<>();
            int i = 1;
            for (; i < lines.length; i++) {
                String line = lines[i];
                if (line.isEmpty()) {
                    break;
                }
                if(!line.contains(": ")) {
                    break;
                }
                String[] header = line.split(": ");
                headers.put(header[0], header[1].substring(0, header[1].length() - 1));
            }

            StringBuilder body = new StringBuilder();
            if (method.equalsIgnoreCase("POST")) {
                for (i = i + 1; i < lines.length; i++) {
                    body.append(lines[i]);
                }
            }

            MyHttpRequest httpRequest = new MyHttpRequest(method, path, headers, body.toString());
            logger.debug(httpRequest.toString());

            return Optional.of(httpRequest);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw e;
        }
    }

    private static String parseMethod(String[] requestLine) {
        String method = requestLine[0];

        return switch (method.toUpperCase()) {
            case "GET" -> "GET";
            case "POST" -> "POST";
            default -> throw new UnsupportedOperationException("Unsupported HTTP method: " + method);
        };
    }
}
