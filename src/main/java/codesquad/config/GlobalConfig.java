package codesquad.config;

import codesquad.http.RequestHandler;
import codesquad.http.UserProcessor;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class GlobalConfig {

    public static final int SERVER_PORT = 8080;
    public static final int REQUEST_THREADS = 10;
    public static final String TIMEZONE = "GMT";
    public static final Locale LOCALE = Locale.US;
    public static final Set<String> DEFAULT_PAGES = Set.of("index.html");
    public static final Map<String, RequestHandler> REQUEST_HANDLER = Map.of("/create", new UserProcessor());

    private GlobalConfig() {
    }
}
