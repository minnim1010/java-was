package codesquad.error;

public class UnSupportedHttpMethodException extends RuntimeException {

    public UnSupportedHttpMethodException() {
    }

    public UnSupportedHttpMethodException(String message) {
        super(message);
    }

    public UnSupportedHttpMethodException(String message, Throwable cause) {
        super(message, cause);
    }
}
