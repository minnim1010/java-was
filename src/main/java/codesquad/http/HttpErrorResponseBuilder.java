package codesquad.http;

import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;
import codesquad.http.property.HttpStatus;
import java.io.IOException;

public class HttpErrorResponseBuilder {

    public byte[] createServerErrorResponse(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        return createErrorResponse(httpRequest, httpResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public byte[] createBadRequestResponse(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        return createErrorResponse(httpRequest, httpResponse, HttpStatus.BAD_REQUEST);
    }

    public byte[] createNotFoundResponse(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        return createErrorResponse(httpRequest, httpResponse, HttpStatus.NOT_FOUND);
    }

    private byte[] createErrorResponse(HttpRequest request, HttpResponse response, HttpStatus status)
            throws IOException {
        response.setVersion(request.version());
        response.setStatus(status);
        return response.format();
    }
}
