package codesquad.http.parser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MultipartFormDataParser {

    private MultipartFormDataParser() {
    }

    public static Map<String, Part> parse(byte[] body, String boundary) {
        Map<String, Part> parts = new HashMap<>();
        byte[] boundaryBytes = ("--" + boundary + "\r\n").getBytes();
        byte[] delimiterBytes = "\r\n\r\n".getBytes();
        byte[] endBoundaryBytes = ("--" + boundary + "--").getBytes();

        int position = 0;
        boolean isLast = false;
        while (position < body.length && !isLast) {
            int boundaryIndex = indexOf(body, boundaryBytes, position);
            if (boundaryIndex == -1) {
                break;
            }

            int endBoundaryIndex = indexOf(body, boundaryBytes, boundaryIndex + 1);
            if (endBoundaryIndex == -1) {
                endBoundaryIndex = indexOf(body, endBoundaryBytes, boundaryIndex);
                isLast = true;
            }
            int startContentIndex = indexOf(body, delimiterBytes, boundaryIndex) + delimiterBytes.length;

            if (startContentIndex == -1 || startContentIndex >= endBoundaryIndex) {
                break;
            }

            byte[] headersBytes = Arrays.copyOfRange(body, boundaryIndex + boundaryBytes.length,
                    startContentIndex - delimiterBytes.length);
            String headers = new String(headersBytes);
            String[] headerLines = headers.split("\r\n");

            String disposition = headerLines[0];
            String contentType = headerLines.length > 1 ? headerLines[1] : null;

            String name = extractName(disposition);
            String filename = extractFilename(disposition);

            byte[] partBody = Arrays.copyOfRange(body, startContentIndex, endBoundaryIndex);

            parts.put(name, new Part(name, filename, partBody, contentType));
            position = endBoundaryIndex;
        }
        return parts;
    }

    private static int indexOf(byte[] array, byte[] target, int start) {
        outerLoop:
        for (int i = start; i <= array.length - target.length; i++) {
            for (int j = 0; j < target.length; j++) {
                if (array[i + j] != target[j]) {
                    continue outerLoop;
                }
            }
            return i;
        }
        return -1;
    }

    private static String extractName(String disposition) {
        String[] tokens = disposition.split(";");
        for (String token : tokens) {
            token = token.trim();
            if (token.startsWith("name=")) {
                return token.substring(6, token.length() - 1);
            }
        }
        return null;
    }

    private static String extractFilename(String disposition) {
        String[] tokens = disposition.split(";");
        for (String token : tokens) {
            token = token.trim();
            if (token.startsWith("filename=")) {
                return token.substring(10, token.length() - 1);
            }
        }
        return null;
    }

    public static class Part {
        private final String name;
        private final String filename;
        private final byte[] body;
        private final String contentType;

        public Part(String name, String filename, byte[] body, String contentType) {
            this.name = name;
            this.filename = filename;
            this.body = body;
            this.contentType = contentType;
        }

        public String getName() {
            return name;
        }

        public String getFilename() {
            return filename;
        }

        public byte[] getBody() {
            return body;
        }

        public String getContentType() {
            return contentType;
        }
    }
}

