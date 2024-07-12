package codesquad;

import codesquad.config.GlobalConstants;
import codesquad.context.WebContext;
import codesquad.http.HttpProcessor;
import codesquad.http.HttpRequestPreprocessor;
import codesquad.http.HttpRequestProcessor;
import codesquad.http.handler.DynamicRequestHandlerResolver;
import codesquad.http.handler.StaticResourceRequestHandler;
import codesquad.http.session.SessionIdGenerator;
import codesquad.http.session.SessionManager;
import codesquad.socket.ClientSocket;
import codesquad.socket.Reader;
import codesquad.socket.ServerSocket;
import codesquad.socket.Writer;
import codesquad.template.NodeProcessor;
import codesquad.template.TemplateEngine;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebServer {

    private static final Logger log = LoggerFactory.getLogger(WebServer.class);

    private final WebContext webContext;
    private final HttpProcessor httpProcessor;

    public WebServer(WebContext webContext) {
        this.webContext = webContext;
        this.httpProcessor = createHttpProcessor(webContext);
    }

    private HttpProcessor createHttpProcessor(WebContext webContext) {
        long sessionTimeout = GlobalConstants.getInstance().getSessionTimeout();
        int sessionPoolMaxSize = GlobalConstants.getInstance().getSessionPoolMaxSize();
        SessionIdGenerator sessionIdGenerator = new SessionIdGenerator();
        SessionManager sessionManager = SessionManager.createInstance(sessionPoolMaxSize, sessionTimeout,
                sessionIdGenerator);

        HttpRequestPreprocessor httpRequestPreprocessor = new HttpRequestPreprocessor(sessionManager);
        DynamicRequestHandlerResolver dynamicRequestHandlerResolver = new DynamicRequestHandlerResolver(
                webContext.getRequestHandlerMap());

        NodeProcessor nodeProcessor = new NodeProcessor();
        TemplateEngine.createInstance(nodeProcessor);
        StaticResourceRequestHandler staticResourceRequestHandler = new StaticResourceRequestHandler(
                webContext.getStaticResourcePaths(), webContext.getDefaultPages());
        HttpRequestProcessor httpRequestProcessor = new HttpRequestProcessor(dynamicRequestHandlerResolver,
                staticResourceRequestHandler);

        return new HttpProcessor(httpRequestPreprocessor, httpRequestProcessor);
    }

    public void run() {
        ExecutorService threadPool = Executors.newFixedThreadPool(GlobalConstants.getInstance().getRequestThreadSize());
        boolean isRunning = true;

        try (ServerSocket serverSocket = new ServerSocket(GlobalConstants.getInstance().getServerPort())) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdownServer(threadPool, serverSocket)));

            while (isRunning) {
                ClientSocket clientSocket = serverSocket.acceptClient();
                threadPool.submit(() -> processClientRequest(clientSocket));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            threadPool.shutdown();
        }
    }

    private void processClientRequest(ClientSocket clientSocket) {
        try (clientSocket) {
            log.debug("Client connected: port " + clientSocket.getPort());

            Reader reader = clientSocket.getReader();
            Writer writer = clientSocket.getWriter();

            httpProcessor.process(reader, writer);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void shutdownServer(ExecutorService threadPool, ServerSocket serverSocket) {
        log.info("Shutting down server...");
        threadPool.shutdown();

        try {
            if (!threadPool.awaitTermination(10, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
                if (!threadPool.awaitTermination(10, TimeUnit.SECONDS)) {
                    log.error("Thread pool did not terminate");
                }
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        } finally {
            try {
                if (!serverSocket.isClosed()) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                log.error("Error closing server socket", e);
            }
        }
        log.info("Server shutdown complete");
    }
}
