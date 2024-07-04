package codesquad;

import codesquad.config.GlobalConfig;
import codesquad.http.HttpErrorResponseBuilder;
import codesquad.http.HttpParser;
import codesquad.http.HttpProcessor;
import codesquad.http.HttpRequestProcessor;
import codesquad.socket.ClientSocket;
import codesquad.socket.ServerSocket;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebServer {

    private static final Logger log = LoggerFactory.getLogger(WebServer.class);
    private static final HttpParser httpParser = new HttpParser();
    private static final HttpRequestProcessor httpRequestProcessor = new HttpRequestProcessor();
    private static final HttpErrorResponseBuilder httpErrorResponseBuilder = new HttpErrorResponseBuilder();
    private static final HttpProcessor httpProcessor = new HttpProcessor(httpParser, httpRequestProcessor,
            httpErrorResponseBuilder);

    public void run() {
        ExecutorService threadPool = Executors.newFixedThreadPool(GlobalConfig.REQUEST_THREADS);
        boolean isRunning = true;

        try (ServerSocket serverSocket = new ServerSocket()) {
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

            String input = clientSocket.read();
            byte[] output = httpProcessor.process(input);
            clientSocket.write(output);
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

