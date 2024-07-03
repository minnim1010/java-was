package codesquad;

import codesquad.error.HttpRequestParseException;
import codesquad.error.ResourceNotFoundException;
import codesquad.http.HttpParser;
import codesquad.http.HttpProcessor;
import codesquad.http.HttpResponseFormatter;
import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;
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
    private static final HttpProcessor httpProcessor = new HttpProcessor();

    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newFixedThreadPool(REQUEST_THREADS);
        boolean isRunning = true;

        try (ServerSocket serverSocket = new ServerSocket();) {
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

    private static void processClientRequest(ClientSocket clientSocket) {
        try (clientSocket) {
            log.debug("Client connected: port " + clientSocket.getPort());

            String input = clientSocket.read();
            String responseStr = processHttp(input);
            clientSocket.write(responseStr);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static String processHttp(String input) {
        HttpResponse response = null;
        String responseStr = null;

        try {
            HttpRequest request = httpParser.parse(input);
            response = httpProcessor.processRequest(request);
        } catch (Exception e) {
            if (e instanceof HttpRequestParseException) {
                responseStr = httpResponseFormatter.createBadRequestResponse();
            } else if (e instanceof ResourceNotFoundException) {
                responseStr = httpResponseFormatter.createNotFoundResponse();
            } else {
                responseStr = httpResponseFormatter.createServerErrorResponse();
            }
            log.error(e.getMessage(), e);
        }

        if (response != null) {
            responseStr = httpResponseFormatter.formatResponse(response);
        }
        return responseStr;
    }

    private static void shutdownServer(ExecutorService threadPool, ServerSocket serverSocket) {
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
    }
}