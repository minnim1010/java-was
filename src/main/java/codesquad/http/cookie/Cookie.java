package codesquad.http.cookie;

public class Cookie {

    private final String name;
    private final String value;
    private final String domain;
    private final String path;
    private final long maxAge;
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
                case "secure":
                    secure = true;
                    break;
                case "httponly":
                    httpOnly = true;
                    break;
            }
        }
        return new Cookie(name, value, domain, path, maxAge, secure, httpOnly);
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
