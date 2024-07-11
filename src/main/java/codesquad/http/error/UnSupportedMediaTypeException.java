package codesquad.http.error;

public class UnSupportedMediaTypeException extends RuntimeException {

    public UnSupportedMediaTypeException(String message) {
        super(message);
    }

    public UnSupportedMediaTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
