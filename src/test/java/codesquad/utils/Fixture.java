package codesquad.utils;

import codesquad.http.message.HttpRequest;
import codesquad.http.property.HttpMethod;
import codesquad.http.property.HttpVersion;
import java.util.HashMap;

public final class Fixture {

    private Fixture() {
    }


    /**
     * Create a HttpRequest object with the following properties: - method: GET - uri: /index.html - version: HTTP/1.1 -
     * headers: Accept: text/html - body: empty
     *
     * @return HttpRequest object
     */
    public static HttpRequest createHttpGetRequest() {
        return new HttpRequest(HttpMethod.GET,
                "/index.html",
                HttpVersion.HTTP_1_1,
                new HashMap<>() {
                    {
                        put("Accept", "text/html");
                    }
                }, "");
    }
}
