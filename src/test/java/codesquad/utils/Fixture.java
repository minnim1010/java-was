package codesquad.utils;

import codesquad.http.message.HttpRequest;
import codesquad.http.property.HttpMethod;
import codesquad.http.property.HttpVersion;
import java.net.URI;
import java.net.URISyntaxException;
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
    public static HttpRequest createHttpGetRequest() throws URISyntaxException {
        return new HttpRequest(HttpMethod.GET,
                new URI("/index.html"),
                HttpVersion.HTTP_1_1,
                new HashMap<>() {
                    {
                        put("Accept", "text/html");
                    }
                }, new byte[0]);
    }

    public static HttpRequest createHttpGetRequest(String uri) throws URISyntaxException {
        return new HttpRequest(HttpMethod.GET,
                new URI(uri),
                HttpVersion.HTTP_1_1,
                new HashMap<>() {
                    {
                        put("Accept", "text/html");
                    }
                }, new byte[0]);
    }
}
