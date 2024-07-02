package codesquad;

import codesquad.http.HttpParser;
import codesquad.http.HttpProcessor;
import codesquad.http.HttpResponseFormatter;
import codesquad.http.MyHttpRequest;
import codesquad.socket.ClientSocket;
import codesquad.socket.ServerSocket;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final HttpParser httpParser = new HttpParser();
    private static final HttpResponseFormatter httpResponseFormatter = new HttpResponseFormatter();
    private static final HttpProcessor httpProcessor = new HttpProcessor(httpResponseFormatter);

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket();) {
            while (true) {
                try {
                    ClientSocket clientSocket = serverSocket.acceptClient();
                    logger.debug("Client connected");

                    String requestStr = clientSocket.read();
                    MyHttpRequest request = httpParser.parse(requestStr)
                            .orElseThrow(() -> new IllegalArgumentException("Invalid request"));

                    String response = httpProcessor.processRequest(request);
                    clientSocket.write(response);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
