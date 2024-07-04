package codesquad.http;

import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;
import codesquad.http.property.HttpStatus;

public class HttpErrorResponseBuilder {

    public HttpResponse createServerErrorResponse(HttpRequest httpRequest, HttpResponse httpResponse) {
        return createErrorResponse(httpRequest, httpResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public HttpResponse createBadRequestResponse(HttpRequest httpRequest, HttpResponse httpResponse) {
        return createErrorResponse(httpRequest, httpResponse, HttpStatus.BAD_REQUEST);
    }

    public HttpResponse createNotFoundResponse(HttpRequest httpRequest, HttpResponse httpResponse) {
        return createErrorResponse(httpRequest, httpResponse, HttpStatus.NOT_FOUND);
    }

    private HttpResponse createErrorResponse(HttpRequest request, HttpResponse response, HttpStatus status) {
        response.setVersion(request.version());
        response.setStatus(status);

        return response;
    }
}
