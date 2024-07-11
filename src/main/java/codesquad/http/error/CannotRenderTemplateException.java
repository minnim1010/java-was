package codesquad.http.error;

public class CannotRenderTemplateException extends RuntimeException {

    public CannotRenderTemplateException(String message) {
        super(message);
    }

    public CannotRenderTemplateException(String message, Throwable cause) {
        super(message, cause);
    }
}
