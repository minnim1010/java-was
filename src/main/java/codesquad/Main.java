package codesquad;

import codesquad.http.HttpParser;
import codesquad.http.HttpProcessor;
import codesquad.http.HttpResponseFormatter;
import codesquad.http.MyHttpRequest;
import codesquad.socket.ClientSocket;
import codesquad.socket.ServerSocket;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main {

    private static final int REQUEST_THREADS = 10;

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final HttpParser httpParser = new HttpParser();
    private static final HttpResponseFormatter httpResponseFormatter = new HttpResponseFormatter();
    private static final HttpProcessor httpProcessor = new HttpProcessor(httpResponseFormatter);

    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newFixedThreadPool(REQUEST_THREADS);

        try (ServerSocket serverSocket = new ServerSocket();) {
            while (true) {
                ClientSocket clientSocket = serverSocket.acceptClient();
                threadPool.submit(() -> {
                    try {
                        int port = clientSocket.getPort();
                        logger.debug("Client connected: port %d", port);

                        String requestStr = clientSocket.read();
                        MyHttpRequest request = httpParser.parse(requestStr)
                                .orElseThrow(() -> new IllegalArgumentException("Invalid request"));

                        String response = httpProcessor.processRequest(request);
                        clientSocket.write(response);
                    } catch (IOException e) {
                        logger.error(e.getMessage());
                        e.printStackTrace();
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                        e.printStackTrace();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
    }
}
