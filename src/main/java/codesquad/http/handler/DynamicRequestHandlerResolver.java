package codesquad.http.handler;

import java.util.Collections;
import java.util.Map;

public class DynamicRequestHandlerResolver {

    private final Map<String, DynamicRequestHandler> requestHandlerMap;

    public DynamicRequestHandlerResolver(Map<String, DynamicRequestHandler> requestHandlerMap) {
        this.requestHandlerMap = Collections.unmodifiableMap(requestHandlerMap);
    }

    public DynamicRequestHandler resolve(String uri) {
        return requestHandlerMap.get(uri);
    }
}
