package codesquad.socket;

import java.io.IOException;
import java.net.Socket;

public class ClientSocket implements AutoCloseable {

    private final Socket nativeSocket;
    private final SocketReader socketReader;
    private final SocketWriter socketWriter;

    public ClientSocket(Socket nativeSocket) throws IOException {
        this.nativeSocket = nativeSocket;
        this.socketReader = new SocketReader(nativeSocket.getInputStream());
        this.socketWriter = new SocketWriter(nativeSocket.getOutputStream());
    }

    public int getPort() {
        return this.nativeSocket.getPort();
    }

    public SocketReader getReader() {
        return socketReader;
    }

    public SocketWriter getWriter() {
        return socketWriter;
    }

    @Override
    public void close() throws IOException {
        this.nativeSocket.close();
    }
}
