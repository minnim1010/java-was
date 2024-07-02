package codesquad;

import codesquad.http.HttpParser;
import codesquad.http.HttpProcessor;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponseFormatter;
import codesquad.socket.ClientSocket;
import codesquad.socket.ServerSocket;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main {

    private static final int REQUEST_THREADS = 10;

    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static final HttpParser httpParser = new HttpParser();
    private static final HttpResponseFormatter httpResponseFormatter = new HttpResponseFormatter();
    private static final HttpProcessor httpProcessor = new HttpProcessor(httpResponseFormatter);

    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newFixedThreadPool(REQUEST_THREADS);
        boolean isRunning = true;

        try (ServerSocket serverSocket = new ServerSocket();) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("Shutting down server...");
                threadPool.shutdown();
                try {
                    if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                        threadPool.shutdownNow();
                        if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                            log.error("Thread pool did not terminate");
                        }
                    }
                } catch (InterruptedException ie) {
                    threadPool.shutdownNow();
                    Thread.currentThread().interrupt();
                } finally {
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        log.error("Error closing server socket", e);
                    }
                }
                log.info("Server shutdown complete");
            }));

            while (isRunning) {
                ClientSocket clientSocket = serverSocket.acceptClient();
                threadPool.submit(() -> {
                    try (clientSocket) {
                        log.debug("Client connected: port " + clientSocket.getPort());

                        String requestStr = clientSocket.read();
                        HttpRequest request = httpParser.parse(requestStr)
                                .orElseThrow(() -> new IllegalArgumentException("Invalid request"));

                        String response = httpProcessor.processRequest(request);
                        clientSocket.write(response);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        e.printStackTrace();
                    }
                });
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
    }
}
