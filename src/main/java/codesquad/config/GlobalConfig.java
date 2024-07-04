package codesquad.config;

import java.util.Locale;

public final class GlobalConfig {

    public static final int SERVER_PORT = 8080;
    public static final int REQUEST_THREADS = 10;
    public static final String TIMEZONE = "GMT";
    public static final Locale LOCALE = Locale.US;

    private GlobalConfig() {
    }
}
