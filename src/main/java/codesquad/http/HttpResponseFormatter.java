package codesquad.http;

import codesquad.http.message.HttpResponse;
import codesquad.http.property.HttpStatus;
import codesquad.http.property.HttpVersion;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map.Entry;

public class HttpResponseFormatter {

    public byte[] formatResponse(HttpResponse httpResponse) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        String statusLine = httpResponse.version().getVersion() + " " +
                httpResponse.status().getCode() + " " +
                httpResponse.status().getMessage() + "\r\n";
        outputStream.write(statusLine.getBytes(StandardCharsets.UTF_8));

        for (Entry<String, String> entry : httpResponse.headers().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            String header = key + ": " + value + "\r\n";
            outputStream.write(header.getBytes(StandardCharsets.UTF_8));
        }

        outputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));

        outputStream.write(httpResponse.body());

        return outputStream.toByteArray();
    }

    public byte[] createServerErrorResponse() throws IOException {
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public byte[] createBadRequestResponse() throws IOException {
        return createErrorResponse(HttpStatus.BAD_REQUEST);
    }

    public byte[] createNotFoundResponse() throws IOException {
        return createErrorResponse(HttpStatus.NOT_FOUND);
    }

    private byte[] createErrorResponse(HttpStatus status) throws IOException {
        return formatResponse(new HttpResponse(
                HttpVersion.HTTP_1_1,
                status,
                new HashMap<>(),
                new byte[0]));
    }
}
