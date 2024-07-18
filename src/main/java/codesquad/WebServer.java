package codesquad;

import codesquad.config.WasConfiguration;
import codesquad.context.WebContext;
import codesquad.http.HttpProcessor;
import codesquad.socket.ClientSocket;
import codesquad.socket.ServerSocket;
import codesquad.socket.SocketReader;
import codesquad.socket.SocketWriter;
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

    public WebServer(WebContext webContext, HttpProcessor httpProcessor) {
        this.webContext = webContext;
        this.httpProcessor = httpProcessor;
    }

    public void run() {
        ExecutorService threadPool = Executors.newFixedThreadPool(
                WasConfiguration.getInstance().getRequestThreadSize());
        boolean isRunning = true;

        try (ServerSocket serverSocket = new ServerSocket(WasConfiguration.getInstance().getServerPort())) {
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

            SocketReader socketReader = clientSocket.getReader();
            SocketWriter socketWriter = clientSocket.getWriter();

            httpProcessor.process(socketReader, socketWriter);
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
