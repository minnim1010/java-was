package codesquad;

import codesquad.config.WasConfiguration;
import codesquad.context.WebContext;
import codesquad.http.HttpProcessor;
import codesquad.http.HttpRequestPreprocessor;
import codesquad.http.HttpRequestProcessor;
import codesquad.http.handler.DynamicRequestHandlerResolver;
import codesquad.http.handler.StaticResourceRequestHandler;
import codesquad.http.session.SessionIdGenerator;
import codesquad.http.session.SessionManager;
import codesquad.template.NodeProcessor;
import codesquad.template.TemplateEngine;

public class WebServerFactory {

    private WebServerFactory() {
    }

    public static WebServer createWebServer(WebContext webContext) {
        HttpProcessor httpProcessor = createHttpProcessor(webContext);
        return new WebServer(webContext, httpProcessor);
    }

    private static HttpProcessor createHttpProcessor(WebContext webContext) {
        SessionManager sessionManager = createSessionManager();
        HttpRequestPreprocessor httpRequestPreprocessor = createHttpRequestPreprocessor(sessionManager);
        DynamicRequestHandlerResolver dynamicRequestHandlerResolver = createDynamicRequestHandlerResolver(webContext);
        StaticResourceRequestHandler staticResourceRequestHandler = createStaticResourceRequestHandler(webContext);
        HttpRequestProcessor httpRequestProcessor = createHttpRequestProcessor(dynamicRequestHandlerResolver,
                staticResourceRequestHandler);

        return new HttpProcessor(httpRequestPreprocessor, httpRequestProcessor);
    }

    private static SessionManager createSessionManager() {
        long sessionTimeout = WasConfiguration.getInstance().getSessionTimeout();
        int sessionPoolMaxSize = WasConfiguration.getInstance().getSessionPoolMaxSize();
        SessionIdGenerator sessionIdGenerator = new SessionIdGenerator();
        return SessionManager.createInstance(sessionPoolMaxSize, sessionTimeout, sessionIdGenerator);
    }

    private static HttpRequestPreprocessor createHttpRequestPreprocessor(SessionManager sessionManager) {
        return new HttpRequestPreprocessor(sessionManager);
    }

    private static DynamicRequestHandlerResolver createDynamicRequestHandlerResolver(WebContext webContext) {
        return new DynamicRequestHandlerResolver(webContext.getRequestHandlerMap());
    }

    private static StaticResourceRequestHandler createStaticResourceRequestHandler(WebContext webContext) {
        return new StaticResourceRequestHandler(webContext.getStaticResourcePaths(), webContext.getDefaultPages());
    }

    private static HttpRequestProcessor createHttpRequestProcessor(
            DynamicRequestHandlerResolver dynamicRequestHandlerResolver,
            StaticResourceRequestHandler staticResourceRequestHandler) {
        NodeProcessor nodeProcessor = new NodeProcessor();
        TemplateEngine.createInstance(nodeProcessor);
        return new HttpRequestProcessor(dynamicRequestHandlerResolver, staticResourceRequestHandler);
    }
}
