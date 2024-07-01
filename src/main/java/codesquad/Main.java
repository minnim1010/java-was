package codesquad;

import codesquad.parser.HttpParser;
import codesquad.parser.MyHttpRequest;
import codesquad.socket.ClientSocket;
import codesquad.socket.ServerSocket;
import java.io.IOException;
import java.net.URISyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final HttpParser httpParser = new HttpParser();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket();) {
            while (true) {
                try {
                    ClientSocket clientSocket = serverSocket.acceptClient();
                    logger.debug("Client connected");

                    String requestStr = clientSocket.read();
                    MyHttpRequest request = httpParser.parse(requestStr)
                                    .orElseThrow(() -> new IllegalArgumentException("Invalid request"));

                    clientSocket.write("message");
                } catch (URISyntaxException | IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }
}
