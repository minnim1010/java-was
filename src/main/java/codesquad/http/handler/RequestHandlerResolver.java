package codesquad.http.handler;

import java.util.Collections;
import java.util.Map;

public class RequestHandlerResolver {

    private final Map<String, RequestHandler> requestHandlerMap;

    public RequestHandlerResolver(Map<String, RequestHandler> requestHandlerMap) {
        this.requestHandlerMap = Collections.unmodifiableMap(requestHandlerMap);
    }

    public RequestHandler resolve(String uri) {
        return requestHandlerMap.get(uri);
    }
}
