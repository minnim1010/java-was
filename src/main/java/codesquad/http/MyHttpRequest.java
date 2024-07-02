package codesquad.http;

import java.util.Map;

public record MyHttpRequest(String method,
                            String path,
                            Map<String, String> headers,
                            String body) {

    public String getHeader(String key) {
        return headers.get(key);
    }

    @Override
    public String toString() {
        return "MyHttpRequest{ " + method + ' ' + path + '\n' +
                headers.toString() + "\n" +
                ", body='" + body + '\'' +
                '}';
    }
}
