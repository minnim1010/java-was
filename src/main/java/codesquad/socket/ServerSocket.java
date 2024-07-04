package codesquad.socket;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerSocket implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(ServerSocket.class);
    private static final int SERVER_PORT = 8080;

    private java.net.ServerSocket serverSocket;

    public ServerSocket() throws IOException {
        this.serverSocket = new java.net.ServerSocket(SERVER_PORT);
        log.info("Listening for connection on port " + SERVER_PORT);
    }

    public ClientSocket acceptClient() throws IOException {
        Socket clientSocket = this.serverSocket.accept();
        return new ClientSocket(clientSocket);
    }

    @Override
    public void close() throws IOException {
        serverSocket.close();
    }
}
