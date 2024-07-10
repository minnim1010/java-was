package codesquad.http.handler;

import java.util.Map;

public class RequestHandlerResolver {

    private final Map<String, RequestHandler> requestHandlerMap;

    public RequestHandlerResolver(Map<String, RequestHandler> requestHandlerMap) {
        this.requestHandlerMap = requestHandlerMap;
    }

    public RequestHandler resolve(String uri) {
        return requestHandlerMap.get(uri);
    }
}
