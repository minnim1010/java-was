package codesquad.socket;

import java.io.IOException;
import java.net.Socket;

public class ClientSocket implements AutoCloseable {

    private final Socket nativeSocket;
    private final Reader reader;
    private final Writer writer;

    public ClientSocket(Socket nativeSocket) throws IOException {
        this.nativeSocket = nativeSocket;
        this.reader = new Reader(nativeSocket.getInputStream());
        this.writer = new Writer(nativeSocket.getOutputStream());
    }

    public int getPort() {
        return this.nativeSocket.getPort();
    }

    public Reader getReader() {
        return reader;
    }

    public Writer getWriter() {
        return writer;
    }

    @Override
    public void close() throws IOException {
        this.nativeSocket.close();
    }
}
