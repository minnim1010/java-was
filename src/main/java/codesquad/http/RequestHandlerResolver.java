package codesquad.http;

import codesquad.config.GlobalConfig;

public class RequestHandlerResolver {

    public RequestHandler resolve(String uri) {
        return GlobalConfig.REQUEST_HANDLER.get(uri);
    }
}
