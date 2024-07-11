package codesquad.http.cookie;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class Cookie {

    private static final DateTimeFormatter RFC_1123_DATE_TIME_FORMATTER =
            DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneId.of("GMT"));

    private final String name;
    private final String value;
    private final String domain;
    private final String path;
    private final long maxAge;
    private final Instant expires;
    private final boolean secure;
    private final boolean httpOnly;

    public Cookie(String name,
                  String value,
                  String domain,
                  String path,
                  long maxAge,
                  boolean secure,
                  boolean httpOnly) {
        this.name = name;
        this.value = value;
        this.domain = domain;
        this.path = path;
        this.maxAge = maxAge;
        this.expires = Instant.now().plus(maxAge, ChronoUnit.SECONDS);
        this.secure = secure;
        this.httpOnly = httpOnly;
    }

    private Cookie(String name,
                   String value,
                   String domain,
                   String path,
                   long maxAge,
                   Instant expires,
                   boolean secure,
                   boolean httpOnly) {
        this.name = name;
        this.value = value;
        this.domain = domain;
        this.path = path;
        this.maxAge = maxAge;
        this.expires = expires;
        this.secure = secure;
        this.httpOnly = httpOnly;
    }

    public static Cookie parse(String cookie) {
        String[] cookieParts = cookie.split(";");
        String[] nameAndValue = cookieParts[0].split("=");
        String name = nameAndValue[0];
        String value = nameAndValue[1];
        String domain = null;
        String path = null;
        long maxAge = -1;
        boolean secure = false;
        boolean httpOnly = false;
        Instant expires = null;
        for (int i = 1; i < cookieParts.length; i++) {
            String[] cookiePart = cookieParts[i].trim().split("=");
            switch (cookiePart[0].toLowerCase()) {
                case "domain":
                    domain = cookiePart[1];
                    break;
                case "path":
                    path = cookiePart[1];
                    break;
                case "max-age":
                    maxAge = Long.parseLong(cookiePart[1]);
                    break;
                case "expires":
                    expires = parseExpires(cookiePart[1]);
                    break;
                case "secure":
                    secure = true;
                    break;
                case "httponly":
                    httpOnly = true;
                    break;
            }
        }
        return new Cookie(name, value, domain, path, maxAge, expires, secure, httpOnly);
    }

    public static Instant parseExpires(String expires) {
        try {
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(expires, RFC_1123_DATE_TIME_FORMATTER);
            return zonedDateTime.toInstant();
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid Expires date format", e);
        }
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getDomain() {
        return domain;
    }

    public String getPath() {
        return path;
    }

    public long getMaxAge() {
        return maxAge;
    }

    public Instant getExpires() {
        return expires;
    }

    public String formatExpires() {
        return RFC_1123_DATE_TIME_FORMATTER.format(this.expires);
    }

    public boolean isSecure() {
        return secure;
    }

    public boolean isHttpOnly() {
        return httpOnly;
    }

    @Override
    public String toString() {
        return "Cookie{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", domain='" + domain + '\'' +
                ", path='" + path + '\'' +
                ", maxAge=" + maxAge +
                ", secure=" + secure +
                ", httpOnly=" + httpOnly +
                '}';
    }
}
