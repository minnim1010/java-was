package codesquad.socket;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerSocket implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(ServerSocket.class);

    private final int port;
    private final java.net.ServerSocket nativeServerSocket;

    public ServerSocket(int port) throws IOException {
        this.port = port;
        this.nativeServerSocket = new java.net.ServerSocket(port);
        log.info("Listening for connection on port " + port);
    }

    public ClientSocket acceptClient() throws IOException {
        Socket clientSocket = this.nativeServerSocket.accept();
        return new ClientSocket(clientSocket);
    }

    @Override
    public void close() throws IOException {
        nativeServerSocket.close();
    }

    public boolean isClosed() {
        return nativeServerSocket.isClosed();
    }
}
