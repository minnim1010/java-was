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
